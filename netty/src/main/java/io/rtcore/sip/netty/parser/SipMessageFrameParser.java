package io.rtcore.sip.netty.parser;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.Optional;
import java.util.OptionalInt;

import com.google.common.base.CharMatcher;
import com.google.common.primitives.UnsignedInts;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.buffer.search.AhoCorasicSearchProcessorFactory;
import io.netty.buffer.search.AhoCorasicSearchProcessorFactory.Processor;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ByteProcessor;
import io.netty.util.ByteProcessor.IndexOfProcessor;
import io.rtcore.sip.netty.codec.DefaultSipHeaders;
import io.rtcore.sip.netty.codec.SipConstants;
import io.rtcore.sip.netty.codec.SipHeaders;
import io.rtcore.sip.netty.codec.SipMethod;

public class SipMessageFrameParser {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SipMessageFrameParser.class);

  private static final ByteBuf SIP_20 = Unpooled.wrappedBuffer("SIP/2.0".getBytes()).asReadOnly();
  private static final CharMatcher DIGIT = CharMatcher.inRange('0', '9');
  private static final ByteProcessor FIND_COLON = new IndexOfProcessor(SipConstants.COLON);

  private static final AhoCorasicSearchProcessorFactory END_OF_LINE =
    AhoCorasicSearchProcessorFactory.newAhoCorasicSearchProcessorFactory("\r\n".getBytes(), "\n".getBytes());

  private static final AhoCorasicSearchProcessorFactory END_OF_HEADERS =
    AhoCorasicSearchProcessorFactory.newAhoCorasicSearchProcessorFactory("\r\n\r\n".getBytes(), "\n\n".getBytes());

  private static final AhoCorasicSearchProcessorFactory HSEP =
    AhoCorasicSearchProcessorFactory.newAhoCorasicSearchProcessorFactory(":".getBytes(), " ".getBytes(), "\t".getBytes());

  public static ByteBuf readInitialLine(ByteBuf buf) {
    Processor processor = END_OF_LINE.newSearchProcessor();
    int idx = buf.forEachByte(processor);
    if (idx == -1) {
      return null;
    }
    int len =
      processor.getFoundNeedleId() == 0 ? 2
                                        : 1;
    ByteBuf res = buf.readSlice((idx - buf.readerIndex() - len) + 1);
    buf.skipBytes(len);
    return res;
  }

  public static ByteBuf readHeaders(ByteBuf buf) {
    Processor processor = END_OF_HEADERS.newSearchProcessor();
    int idx = buf.forEachByte(processor);
    if (idx == -1) {
      return null;
    }

    if (processor.getFoundNeedleId() == 0) {
      int len = 4;
      ByteBuf res = buf.readSlice(((idx - buf.readerIndex() - (len)) + 1 + 2));
      buf.skipBytes(len - 2);
      return res;
    }
    else {
      int len = 2;
      ByteBuf res = buf.readSlice(((idx - buf.readerIndex() - (len)) + 1 + 1));
      buf.skipBytes(len - 1);
      return res;
    }

  }

  /**
   * 
   * @param buf
   * @return
   */

  public static ByteBuf parseHeaders(ByteBuf buf) {

    // read the initial line, searching for the '\n'. only allow max of 1024 bytes.
    int eol = buf.indexOf(buf.readerIndex(), Math.min(buf.readerIndex() + 1024, buf.writerIndex()), (byte) '\n');

    // the end of line.
    if (eol == -1) {
      // invalid message.
      log.warn("invalid SIP message? no LF found");
      return null;
    }

    int rlen = eol - buf.readerIndex();

    if (rlen < 11) {
      // invalid message.
      log.warn("invalid initial SIP line length ({})", rlen);
      return null;
    }

    //
    ByteBuf initialLine = buf.slice(buf.readerIndex(), eol - buf.readerIndex());

    if (initialLine.getByte(initialLine.writerIndex() - 1) == '\r') {
      // remove the CR if there is one (there should be ...).
      initialLine.writerIndex(initialLine.writerIndex() - 1);
    }

    // skip over the LF, the next chat should be start of headers.
    buf.readerIndex(eol + 1);

    //
    DefaultSipHeaders headers = new DefaultSipHeaders(false);

    // chars between readerIndex and the LF should only be allowed chars, except final one which can
    // be \r.

    if (ByteBufUtil.equals(initialLine, initialLine.writerIndex() - SIP_20.readableBytes(), SIP_20, 0, SIP_20.readableBytes())) {

      final int spos = initialLine.writerIndex() - SIP_20.readableBytes() - 1;

      // should be a SP directly before.
      if (initialLine.getByte(spos) != ' ') {
        // missing space.
        log.info("missing SP before SIP/2.0, got '{}'", Byte.toString(initialLine.getByte(spos)));
        return null;
      }

      // we can trim from the initialLine.
      initialLine.writerIndex(initialLine.writerIndex() - SIP_20.readableBytes() - 1);

      String line = initialLine.toString(UTF_8);
      String[] parts = line.split(" ", 2);

      //
      headers.add(":method", parts[0]);

      // unescape.
      headers.add(":uri", parts[1]);

    }
    else if (ByteBufUtil.equals(initialLine, initialLine.readerIndex(), SIP_20, 0, SIP_20.readableBytes())) {

      final int spos = (initialLine.readerIndex() - SIP_20.readableBytes()) + 1;

      // should be a SP directly after.
      if (initialLine.getByte(spos) != ' ') {
        // missing space.
        log.info("missing SP after SIP/2.0: {}", Byte.toString(initialLine.getByte(spos)));
        return null;
      }

      String line = initialLine.toString(UTF_8);

      String[] parts = line.split(" ", 2);

      // should be exactly 3 digits, 100 to 699.
      if ((parts[0].length() != 3) && DIGIT.matchesAllOf(parts[0])) {
        log.info("invalid status code: '{}'", parts[0]);
        return null;
      }

      //
      int code = UnsignedInts.parseUnsignedInt(parts[0]);

      if ((code < 100) || (code > 699)) {
        log.info("invalid status code: {}", code);
        return null;
      }

      headers.add(":status", parts[0]);

      parts[1] = parts[1].trim();

      if (!parts[1].isEmpty()) {
        headers.add(":reason", parts[1]);
      }

    }
    else {

      log.info("unrecognized SIP packet: [{}]", initialLine.toString(UTF_8));
      return null;

    }

    // now read headers. should return with readerIndex set to start of body (or end, if none).
    if (!readHeaders(headers, buf)) {
      log.warn("failed to read headers");
      return null;
    }

    //

    //
    OptionalInt contentLength = contentLength(headers);

    log.trace("msg [{}] contentLength = {}, remaining = {}", toString(headers), contentLength, buf.readableBytes());

    // headers.forEach(e -> log.info("[{}] = [{}]", e.getKey(), e.getValue()));

    // Maps.immutableEntry(headers, buf);
    return null;

  }

  private static CharSequence toString(DefaultSipHeaders headers) {
    if (headers.contains(":method")) {
      return String.format("%s %s SIP/2.0", headers.get(":method"), headers.get(":uri"));
    }
    else if (headers.contains(":reason")) {
      return String.format("SIP/2.0 %s %s", headers.get(":status"), headers.get(":reason"));
    }
    return String.format("SIP/2.0 %s", headers.get(":status"));
  }

  private static OptionalInt contentLength(DefaultSipHeaders headers) {

    //
    CharSequence contentLength = Optional.ofNullable(headers.get("content-length")).orElseGet(() -> headers.get("l"));

    if (contentLength != null) {
      return OptionalInt.of(Integer.parseUnsignedInt(contentLength, 0, contentLength.length(), 10));
    }

    return OptionalInt.empty();

  }

  /**
   * 
   */

  /**
   * 
   * @param buf
   * @return
   */

  private static boolean readHeaders(SipHeaders headers, ByteBuf buf) {

    // need at least 6 chars: hname, COLON, and then CRLFCRLF.

    while (buf.isReadable(6)) {

      ByteBuf hdr = readHeader(buf);

      if (hdr == null) {
        break;
      }

      addHeader(headers, hdr);

    }

    if (buf.isReadable(2) && (buf.getByte(buf.readerIndex()) == SipConstants.CR) && (buf.getByte(buf.readerIndex() + 1) == SipConstants.LF)) {
      buf.skipBytes(2);
      return true;
    }

    return false;

  }

  private static boolean addHeader(SipHeaders headers, ByteBuf hdr) {

    int idx = hdr.indexOf(hdr.readerIndex(), hdr.writerIndex(), SipConstants.COLON);

    if (idx == -1) {
      return false;
    }

    int hnlen = idx - hdr.readerIndex();

    // ensure sufficient length
    if (hnlen == 0) {
      return false;
    }

    CharSequence name = hdr.readCharSequence(hnlen, UTF_8);
    hdr.skipBytes(1);

    while (hdr.isReadable() && isWhitespace(hdr.getByte(hdr.readerIndex()))) {
      hdr.skipBytes(1);
    }

    headers.add(name, hdr.readCharSequence(hdr.readableBytes(), UTF_8));

    return true;

  }

  private static boolean isWhitespace(byte b) {
    return b == ' ';
  }

  private static ByteBuf readHeader(ByteBuf buf) {

    // find next LF.
    int end = buf.indexOf(buf.readerIndex(), buf.writerIndex(), SipConstants.LF);

    if (end == -1) {
      return null;
    }

    int len = (end - buf.readerIndex());

    if (len < 2) {
      return null;
    }

    ByteBuf content = buf.readSlice(len);

    // skip over the LF.
    buf.skipBytes(1);

    if (content.getByte(content.writerIndex() - 1) == SipConstants.CR) {
      content.writerIndex(content.writerIndex() - 1);
    }

    return content;

  }

  public static boolean isValid(ByteBuf initialLine) {
    if (initialLine == null) {
      return false;
    }
    return true;
  }

  public static boolean isRequestLine(ByteBuf initialLine) {
    return initialLine.isReadable(SIP_20.readableBytes() + 2)
      &&
      ByteBufUtil.equals(initialLine, initialLine.writerIndex() - SIP_20.readableBytes(), SIP_20, 0, SIP_20.readableBytes())
      &&
      (initialLine.getByte(initialLine.writerIndex() - SIP_20.readableBytes() - 1) == SipConstants.SP);
  }

  public static boolean isResponseLine(ByteBuf initialLine) {
    return initialLine.isReadable(SIP_20.readableBytes() + 2)
      &&
      ByteBufUtil.equals(initialLine, initialLine.readerIndex(), SIP_20, 0, SIP_20.readableBytes())
      &&
      (initialLine.getByte(initialLine.readerIndex() + SIP_20.readableBytes()) == SipConstants.SP);
  }

  /**
   * convert to netty header set.
   * 
   * @param headers
   * @return
   */

  public static DefaultSipHeaders toHeaders(ByteBuf input) {

    // duplicate so we don't affect input buffer.
    input = input.duplicate();

    // run through each header. splitting

    DefaultSipHeaders hdrs = new DefaultSipHeaders(true);

    while (input.isReadable()) {

      ByteBuf buf = readSingleHeader(input);

      if (buf == null) {
        throw new IllegalArgumentException("failed to locate end of headers");
      }

      // now find the first of [ :\t].

      Processor processor = HSEP.newSearchProcessor();
      int hnamend = buf.forEachByte(processor);

      if (hnamend == -1) {
        throw new IllegalArgumentException("invalid header: missing COLON");
      }

      CharSequence name = buf.readCharSequence(hnamend - buf.readerIndex(), UTF_8);
      buf.skipBytes(1);
      // System.err.println("NAME: " + name);

      if (processor.getFoundNeedleId() > 0) {
        // we need to skip to the ':'
        int idx = buf.forEachByte(FIND_COLON);
        if (idx == -1) {
          throw new IllegalArgumentException("missing COLON");
        }
        buf.readerIndex(idx + 1);
      }

      // now skip LWS.
      int start = buf.forEachByte(ByteProcessor.FIND_NON_LINEAR_WHITESPACE);
      if (start != -1) {
        buf.readerIndex(start);
      }

      // System.err.println(String.format("[%s] = [%s]", name, buf.toString(UTF_8)));
      // System.err.println(ByteBufUtil.prettyHexDump(buf));

      hdrs.add(name, buf.readCharSequence(buf.readableBytes(), UTF_8));

    }

    return hdrs;

  }

  private static ByteBuf readSingleHeader(ByteBuf input) {

    Processor processor = END_OF_LINE.newSearchProcessor();

    int idx = input.forEachByte(processor);

    if (idx == -1) {
      // no end of line. hmm, invalid.
      return null;
    }

    int len =
      processor.getFoundNeedleId() == 0 ? 2
                                        : 1;

    ByteBuf res = input.readSlice((idx - input.readerIndex() - len) + 1);
    input.skipBytes(len);
    // TODO: multi-line. although zero devices use it in the wild.
    return res;

  }

  private void prepareSip(DatagramPacket pkt) {

    //
    ByteBuf buf = pkt.content().duplicate();

    //
    // Hasher hasher = Hashing.farmHashFingerprint64().newHasher();
    // buf.forEachByte(b -> {
    // hasher.putByte(b);
    // return true;
    // });
    // HashCode hash = hasher.hash();

    // parse into the constituent parts.
    ByteBuf initialLine = SipMessageFrameParser.readInitialLine(buf);

    final String[] parts;

    if (SipMessageFrameParser.isRequestLine(initialLine)) {

      parts = initialLine.toString(UTF_8).split("[ \t]+", 3);

      if ((parts.length != 3) || !parts[2].contentEquals("SIP/2.0")) {
        log.info("missing SIP/2.0 prefix on request line");
        return;
      }

    }
    else if (SipMessageFrameParser.isResponseLine(initialLine)) {

      // a sane upper bound on the initial line before we treat as malformed.
      if (initialLine.readableBytes() > 512) {
        log.info("got oversize ({} byte) SIP response line - dropping", buf.readableBytes());
        return;
      }

      // split.

      parts = initialLine.toString(UTF_8).split("[ \t]+", 3);

      if ((parts.length != 3) || !parts[0].contentEquals("SIP/2.0")) {
        log.info("missing SIP/2.0 prefix on request line, got '{}'", parts[0]);
        return;
      }

    }
    else {
      log.info("invalid initial line: '{}'", initialLine.toString(UTF_8).substring(0, 128));
      return;
    }

    ByteBuf headerBuf = SipMessageFrameParser.readHeaders(buf);

    if (!parts[0].contentEquals("SIP/2.0")) {

      SipMethod method = SipMethod.valueOf(parts[0]);

      if (method == null) {
        // we know all of our methods. don't preetnd we're actually extensible.
        log.warn("early drop of unknown SIP method '{}'", parts[0]);
        return;
      }

      // final Uri uri;
      //
      // try {
      // uri = messageManager.parseUri(parts[1]);
      // }
      // catch (Exception ex) {
      // log.info("Invalid URI in request: '{}'", parts[1]);
      // return;
      // }

      // prepareRequest(pkt, method, uri, headerBuf, buf);

    }
    else {

      int statusCode = UnsignedInts.parseUnsignedInt(parts[1]);

      if ((statusCode < 100) || (statusCode > 699)) {
        log.warn("invalid SIP response code '{}'", parts[1]);
        return;
      }

      // prepareResponse(pkt, statusCode, parts[2], headerBuf, buf);

    }

  }

}
