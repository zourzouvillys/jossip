package io.rtcore.sip.netty.codec;

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
import io.rtcore.sip.common.ImmutableSipHeaderLine;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.frame.ImmutableSipRequestFrame;
import io.rtcore.sip.frame.ImmutableSipResponseFrame;

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

  private final SipLineParser lineParser;
  private final SipHeaderParser headerParser;
  private String name = null;
  private String value = null;
  private SipInitialLine initialLine = null;
  private ArrayList<SipHeaderLine> headers = new ArrayList<>();
  private int contentLength = 0;

  public SipStreamDecoder() {
    this(8192, 1024);
  }

  public SipStreamDecoder(final int maxMessageSize) {
    this(maxMessageSize, 1024);
  }

  public SipStreamDecoder(final int maxMessageSize, final int initialBufferSize) {

    super.setSingleDecode(true);

    // should be large enough for most common headers.
    final AppendableCharSequence seq = new AppendableCharSequence(256);

    this.lineParser = new SipLineParser(seq, this.maxInitialLine);
    this.headerParser = new SipHeaderParser(seq, this.maxHeaderBytes);

  }

  @Override
  protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) throws Exception {
    while (in.isReadable()) {
      switch (this.currentState) {
        case NONE:
          if (!this.readNone(ctx, in, out)) {
            return;
          }
          break;
        case READING_INITIAL:
          if (!this.readInitialLine(ctx, in, out)) {
            return;
          }
          break;
        case READING_HEADERS:
          if (!this.readHeaders(ctx, in, out)) {
            return;
          }
          break;
        case READING_BODY:
          if (!this.readBody(ctx, in, out)) {
            return;
          }
          break;
        default:
          throw new IllegalArgumentException();
      }
    }
  }

  private boolean readNone(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) {

    int count = 0;

    while (buffer.isReadable(2) && (buffer.getByte(buffer.readerIndex()) == '\r')) {

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

  private boolean readInitialLine(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) {

    final AppendableCharSequence line = this.lineParser.parse(buffer);

    if (line == null) {
      // not enough data yet. wait, grasshopper.
      return false;
    }

    this.initialLine = SipParsingUtils.parseInitialLine(line.toString());
    this.lineParser.reset();
    this.currentState = State.READING_HEADERS;
    return true;

  }

  private boolean readHeaders(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) {

    AppendableCharSequence line = this.headerParser.parse(buffer);

    if (line == null) {
      return false;
    }

    while (line.length() > 0) {

      final char firstChar = line.charAtUnsafe(0);

      if ((this.name != null) && ((firstChar == ' ') || (firstChar == '\t'))) {

        // this is LWS.

        // please do not make one line from below code
        // as it breaks +XX:OptimizeStringConcat optimization
        final String trimmedLine = line.toString().trim();
        final String valueStr = String.valueOf(this.value);
        this.value = valueStr + ' ' + trimmedLine;

        if (this.value.length() > this.maxHeaderBytes) {
          throw new TooLongFrameException("header line too long");
        }

      }
      else {

        if (this.name != null) {
          this.commitHeader();
        }

        if (this.headers.size() >= this.maxHeaderCount) {
          // sanity, avoid too many SIP header lines.
          throw new TooLongFrameException("too many header lines");
        }

        this.splitHeader(line);

      }

      line = this.headerParser.parse(buffer);

      if (line == null) {
        // no line yet
        return false;
      }

    }

    // Add the last header.
    if (this.name != null) {
      this.commitHeader();
    }

    // we have a full set of headers, validate and check to see if we are expecting content.
    this.processHeaders(ctx, buffer, out);

    this.headerParser.reset();

    return true;

  }

  /**
   * attempt to read any remaining bytes needed.
   */

  private boolean readBody(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) {

    Verify.verify(this.contentLength > 0);

    // need more data.
    if (!buffer.isReadable(this.contentLength)) {
      return false;
    }

    final ByteBuf body = buffer.readSlice(this.contentLength);

    if (this.initialLine instanceof final SipInitialLine.RequestLine req) {
      out.add(ImmutableSipRequestFrame.of(req, this.headers, Optional.of(body.toString(StandardCharsets.UTF_8))));
    }
    else if (this.initialLine instanceof final SipInitialLine.ResponseLine res) {
      out.add(ImmutableSipResponseFrame.of(res, this.headers, Optional.of(body.toString(StandardCharsets.UTF_8))));
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

    this.headers.add(ImmutableSipHeaderLine.of(this.name, this.value));
    // reset name and value fields
    this.name = null;
    this.value = null;

  }

  /**
   * process the headers we have received.
   *
   * @param ctx
   * @param buffer
   * @param out
   */

  private void processHeaders(final ChannelHandlerContext ctx, final ByteBuf buffer, final List<Object> out) {

    //// content-length calculation.

    final Set<String> contentLengths =
      this.headers.stream()
        .filter(e -> e.knownHeaderId().filter(id -> id == StandardSipHeaders.CONTENT_LENGTH).isPresent())
        .map(SipHeaderLine::headerValues)
        .collect(Collectors.toSet());

    if (contentLengths.isEmpty()) {

      // urgh: no explicit content length, so assume 0.
      this.contentLength = 0;

      //
      if (this.initialLine instanceof final SipInitialLine.RequestLine req) {
        out.add(ImmutableSipRequestFrame.of(req, this.headers, Optional.empty()));
      }
      else if (this.initialLine instanceof final SipInitialLine.ResponseLine res) {
        out.add(ImmutableSipResponseFrame.of(res, this.headers, Optional.empty()));
      }
      else {
        throw new IllegalArgumentException();
      }

    }
    else if (contentLengths.size() == 1) {

      final String firstField = contentLengths.iterator().next();

      if (!Character.isDigit(firstField.charAt(0))) {
        throw new CorruptedFrameException("content-length value is not a number: " + firstField);
      }

      final int value = Integer.parseUnsignedInt(firstField);

      if (value == 0) {

        if (this.initialLine instanceof final SipInitialLine.RequestLine req) {
          out.add(ImmutableSipRequestFrame.of(req, this.headers, Optional.empty()));
        }
        else if (this.initialLine instanceof final SipInitialLine.ResponseLine res) {
          out.add(ImmutableSipResponseFrame.of(res, this.headers, Optional.empty()));
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
      else if (value > this.maxContentLength) {
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
  private void splitHeader(final AppendableCharSequence line) {

    final String[] p = line.toString().split(":", 2);

    this.name = p[0].trim();
    this.value = p[1].trim();

  }

}
