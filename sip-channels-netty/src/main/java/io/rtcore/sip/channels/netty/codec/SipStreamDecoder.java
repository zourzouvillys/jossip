package io.rtcore.sip.channels.netty.codec;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Verify;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.AppendableCharSequence;
import io.rtcore.sip.channels.api.ImmutableSipRequestFrame;
import io.rtcore.sip.channels.api.ImmutableSipResponseFrame;
import io.rtcore.sip.common.ImmutableSipHeaderLine;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.StandardSipHeaders;

public class SipStreamDecoder extends ByteToMessageDecoder {

  // longest initial line.
  private final int maxInitialLine = 1024;

  // maximum numbers of header count.
  private final int maxHeaderCount = 256;

  // maximum header bytes (including name and LWS over multiple lines).
  private final int maxHeaderBytes = 4096;

  // maximum content length
  private final int maxContentLength = 4096;

  private enum State {

    // no current state. read CRLFs, otherwise start reading initial line.
    NONE,

    // reading the initial request line. this will not be a CRLF.
    READING_INITIAL,

    // we are reading headers. need the CRLF CRLF for the end of headers indicator.
    READING_HEADERS,

    // headers finished, reading content (based on contentLength).
    READING_BODY,

  }

  // current reader state.
  private State currentState = State.NONE;

  private SipLineParser lineParser;
  private SipHeaderParser headerParser;
  private String name = null;
  private String value = null;
  private SipInitialLine initialLine = null;
  private ArrayList<SipHeaderLine> headers = new ArrayList<>();
  private int contentLength = 0;

  public SipStreamDecoder() {
    this(8192, 1024);
  }

  public SipStreamDecoder(int maxMessageSize) {
    this(maxMessageSize, 1024);
  }

  public SipStreamDecoder(final int maxMessageSize, final int initialBufferSize) {

    super.setSingleDecode(true);

    // should be large enough for most common headers.
    AppendableCharSequence seq = new AppendableCharSequence(256);

    this.lineParser = new SipLineParser(seq, maxInitialLine);
    this.headerParser = new SipHeaderParser(seq, maxHeaderBytes);

  }

  @Override
  protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
    while (in.isReadable()) {
      switch (this.currentState) {
        case NONE:
          if (!readNone(ctx, in, out)) {
            return;
          }
          break;
        case READING_INITIAL:
          if (!readInitialLine(ctx, in, out)) {
            return;
          }
          break;
        case READING_HEADERS:
          if (!readHeaders(ctx, in, out)) {
            return;
          }
          break;
        case READING_BODY:
          if (!readBody(ctx, in, out)) {
            return;
          }
          break;
        default:
          throw new IllegalArgumentException();
      }
    }
  }

  private boolean readNone(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {

    int count = 0;

    while (buffer.isReadable(2) && buffer.getByte(buffer.readerIndex()) == '\r') {

      if (buffer.getByte(buffer.readerIndex() + 1) != '\n') {
        throw new DecoderException("cr followed by non lf");
      }

      count++;
      buffer.skipBytes(2);

    }

    if (count > 0) {

      // notify of the keepalive(s).
      ctx.fireUserEventTriggered(SipKeepalive.crlf(count));

    }

    if (!buffer.isReadable()) {
      // nothing else to read right now.
      return false;
    }

    if (buffer.getByte(buffer.readerIndex()) != '\r') {
      // this is not a CRLF, so must be an initial line (or garbage).
      // either way, we move to READING_INITIAL.
      this.currentState = State.READING_INITIAL;
      return true;
    }

    return false;

  }

  private boolean readInitialLine(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {

    AppendableCharSequence line = lineParser.parse(buffer);

    if (line == null) {
      // not enough data yet. wait, grasshopper.
      return false;
    }

    this.initialLine = SipParsingUtils.parseInitialLine(line.toString());
    this.lineParser.reset();
    this.currentState = State.READING_HEADERS;
    return true;

  }

  private boolean readHeaders(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {

    AppendableCharSequence line = headerParser.parse(buffer);

    if (line == null) {
      return false;
    }

    while (line.length() > 0) {

      char firstChar = line.charAtUnsafe(0);

      if (name != null && (firstChar == ' ' || firstChar == '\t')) {

        // this is LWS.

        // please do not make one line from below code
        // as it breaks +XX:OptimizeStringConcat optimization
        String trimmedLine = line.toString().trim();
        String valueStr = String.valueOf(value);
        value = valueStr + ' ' + trimmedLine;

        if (value.length() > this.maxHeaderBytes) {
          throw new TooLongFrameException("header line too long");
        }

      }
      else {

        if (name != null) {
          commitHeader();
        }

        if (headers.size() >= maxHeaderCount) {
          // sanity, avoid too many SIP header lines.
          throw new TooLongFrameException("too many header lines");
        }

        splitHeader(line);

      }

      line = headerParser.parse(buffer);

      if (line == null) {
        // no line yet
        return false;
      }

    }

    // Add the last header.
    if (name != null) {
      commitHeader();
    }

    // we have a full set of headers, validate and check to see if we are expecting content.
    processHeaders(ctx, buffer, out);

    this.headerParser.reset();

    return true;

  }

  /**
   * attempt to read any remaining bytes needed.
   */

  private boolean readBody(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {

    Verify.verify(this.contentLength > 0);

    // need more data.
    if (!buffer.isReadable(this.contentLength)) {
      return false;
    }

    ByteBuf body = buffer.readSlice(this.contentLength);

    if (initialLine instanceof SipInitialLine.RequestLine req) {
      out.add(ImmutableSipRequestFrame.of(req, headers, Optional.of(body.toString(StandardCharsets.UTF_8))));
    }
    else if (initialLine instanceof SipInitialLine.ResponseLine res) {
      out.add(ImmutableSipResponseFrame.of(res, headers, Optional.of(body.toString(StandardCharsets.UTF_8))));
    }
    else {
      throw new IllegalArgumentException();
    }

    this.initialLine = null;
    this.headers = new ArrayList<>();
    this.contentLength = 0;
    this.currentState = State.NONE;

    return true;

  }

  /**
   * 
   * @param name
   * @param value
   */

  private void commitHeader() {

    headers.add(ImmutableSipHeaderLine.of(name, value));
    // reset name and value fields
    name = null;
    value = null;

  }

  /**
   * process the headers we have received.
   * 
   * @param ctx
   * @param buffer
   * @param out
   */

  private void processHeaders(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) {

    //// content-length calculation.

    Set<String> contentLengths =
      headers.stream()
        .filter(e -> e.knownHeaderId().filter(id -> id == StandardSipHeaders.CONTENT_LENGTH).isPresent())
        .map(SipHeaderLine::headerValues)
        .collect(Collectors.toSet());

    if (contentLengths.isEmpty()) {

      // urgh: no explicit content length, so assume 0.
      this.contentLength = 0;

      //
      if (initialLine instanceof SipInitialLine.RequestLine req) {
        out.add(ImmutableSipRequestFrame.of(req, headers, Optional.empty()));
      }
      else if (initialLine instanceof SipInitialLine.ResponseLine res) {
        out.add(ImmutableSipResponseFrame.of(res, headers, Optional.empty()));
      }
      else {
        throw new IllegalArgumentException();
      }

    }
    else if (contentLengths.size() == 1) {

      String firstField = contentLengths.iterator().next();

      if (!Character.isDigit(firstField.charAt(0))) {
        throw new CorruptedFrameException("content-length value is not a number: " + firstField);
      }

      final int value = Integer.parseUnsignedInt(firstField);

      if (value == 0) {

        if (initialLine instanceof SipInitialLine.RequestLine req) {
          out.add(ImmutableSipRequestFrame.of(req, headers, Optional.empty()));
        }
        else if (initialLine instanceof SipInitialLine.ResponseLine res) {
          out.add(ImmutableSipResponseFrame.of(res, headers, Optional.empty()));
        }
        else {
          throw new IllegalArgumentException();
        }

        this.initialLine = null;
        this.contentLength = 0;
        this.headers = new ArrayList<>();
        this.currentState = State.NONE;

      }
      else if (value < 0) {
        throw new CorruptedFrameException("invalid content-length value: " + value);
      }
      else if (value > maxContentLength) {
        throw new CorruptedFrameException("content-length too large: " + value + " bytes");
      }
      else {
        this.contentLength = value;
        this.currentState = State.READING_BODY;
      }

    }
    else {

      this.contentLength = -1;
      // hmm, multiple content length.
      throw new CorruptedFrameException("multiple content-length headers found in message");

    }

  }

  // split a full header.
  private void splitHeader(AppendableCharSequence line) {

    String[] p = line.toString().split(":", 2);

    name = p[0].trim();
    value = p[1].trim();

  }

}
