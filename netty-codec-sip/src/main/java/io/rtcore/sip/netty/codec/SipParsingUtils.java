package io.rtcore.sip.netty.codec;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.CharMatcher;
import com.google.common.primitives.UnsignedInts;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.internal.AppendableCharSequence;
import io.rtcore.sip.common.ImmutableRequestLine;
import io.rtcore.sip.common.ImmutableResponseLine;
import io.rtcore.sip.common.ImmutableSipHeaderLine;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.frame.SipFrameUtils;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ParserInput;
import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.DefaultParserContext;
import io.rtcore.sip.message.parsers.core.ParseFailureException;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.CSeqParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.NameAddrParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ViaParser;

public class SipParsingUtils {

  private static final CharMatcher TOKEN_CHAR = CharMatcher
      .anyOf("-.!%*_+`'~abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
  private static final CharMatcher DIGIT_CHAR = CharMatcher.inRange('0', '9');

  public static SipInitialLine parseInitialLine(String initialLine) {

    if (initialLine.endsWith(" SIP/2.0") && (initialLine.length() >= 12)) { // 12 == min possible,
                                                                            // 'A
      // s: SIP/2.0'

      initialLine = initialLine.substring(0, initialLine.length() - " SIP/2.0".length());

      final int idx = initialLine.indexOf(' ');

      if (idx == -1) {
        throw new DecoderException("invalid initial SIP/2.0 line");
      }

      final String methodToken = initialLine.substring(0, idx);

      if (!TOKEN_CHAR.matchesAllOf(methodToken)) {
        throw new DecoderException("invalid chars in sip method");
      }

      final SipMethodId method = SipMethods.toMethodId(methodToken);

      try {
        final URI uri = new URI(initialLine.substring(idx + 1));
        return ImmutableRequestLine.of(method, uri);
      } catch (final URISyntaxException e) {
        throw new DecoderException("invalid r-uri");
      }

    }
    if (!initialLine.startsWith("SIP/2.0 ") || (initialLine.length() < 11)) {

      // invalid.
      throw new DecoderException("invalid initial SIP/2.0 line: " + initialLine);

    }
    initialLine = initialLine.substring("SIP/2.0 ".length());

    final String statusToken = initialLine.substring(0, 3);

    if (!DIGIT_CHAR.matchesAllOf(statusToken)) {
      throw new DecoderException("invalid status code");
    }

    final int status = UnsignedInts.parseUnsignedInt(statusToken);

    if ((status < 100) || (status > 699)) {
      throw new DecoderException("invalid status code");
    }

    if ((initialLine.length() > 3) && (initialLine.charAt(3) != ' ')) {
      throw new DecoderException("missing space after initial response line status code");
    }

    final Optional<String> reason = Optional.ofNullable(initialLine)
        .filter(line -> line.length() > 4)
        .map(line -> line.substring(4).trim())
        .filter(e -> !e.isBlank())
        .filter(line -> !Optional.ofNullable(SipStatusCodes.forStatusCode(status)).map(SipStatusCodes::reasonPhrase)
            .orElse("").equals(line));

    return ImmutableResponseLine.of(status, reason);

  }

  public static ArrayList<ImmutableSipHeaderLine> parseHeaders(final ByteBuf buffer) {

    final int maxHeaderBytes = 4096;
    final int maxHeaderCount = 1024;

    final AppendableCharSequence seq = new AppendableCharSequence(256);

    final SipHeaderParser headerParser = new SipHeaderParser(seq, 4096);

    AppendableCharSequence line = headerParser.parse(buffer);

    if (line == null) {
      return null;
    }

    final ArrayList<ImmutableSipHeaderLine> headers = new ArrayList<>();

    String name = null;
    String value = null;

    while (line.length() > 0) {

      final char firstChar = line.charAtUnsafe(0);

      if ((name != null) && ((firstChar == ' ') || (firstChar == '\t'))) {

        // this is LWS.

        // please do not make one line from below code
        // as it breaks +XX:OptimizeStringConcat optimization
        final String trimmedLine = line.toString().trim();
        final String valueStr = String.valueOf(value);
        value = valueStr + ' ' + trimmedLine;

        if (value.length() > maxHeaderBytes) {
          throw new TooLongFrameException("header line too long");
        }

      } else {

        if (name != null) {
          headers.add(ImmutableSipHeaderLine.of(name, value));
          name = null;
          value = null;
        }

        if (headers.size() >= maxHeaderCount) {
          // sanity, avoid too many SIP header lines.
          throw new TooLongFrameException("too many header lines");
        }

        final String[] p = line.toString().split(":", 2);

        name = p[0].trim();
        value = p[1].trim();

      }

      line = headerParser.parse(buffer);

      if (line == null) {
        break;
      }

    }

    // Add the last header.
    if (name != null) {
      headers.add(ImmutableSipHeaderLine.of(name, value));
      name = null;
      value = null;
    }

    // we have a full set of headers, validate and check to see if we are expecting
    // content.
    // processHeaders(ctx, buffer, out);

    // this.headerParser.reset();

    return headers;

  }

  public static OptionalInt readContentLength(final ArrayList<ImmutableSipHeaderLine> headers) {

    final Set<String> contentLengths = headers.stream()
        .filter(e -> e.knownHeaderId().filter(id -> id == StandardSipHeaders.CONTENT_LENGTH).isPresent())
        .map(SipHeaderLine::headerValues)
        .collect(Collectors.toSet());

    if (contentLengths.isEmpty() || (contentLengths.size() != 1)) {

      return OptionalInt.empty();

    }
    final String firstField = contentLengths.iterator().next();

    if (!Character.isDigit(firstField.charAt(0))) {
      throw new CorruptedFrameException("content-length value is not a number: " + firstField);
    }

    final int value = Integer.parseUnsignedInt(firstField);

    if (value < 0) {
      throw new CorruptedFrameException("invalid content-length value: " + value);

    }
    return OptionalInt.of(value);

  }

  /**
   * return the to tag for this message.
   */

  public static Optional<String> toTag(final List<SipHeaderLine> headerLines) {
    return headerLines.stream()
        .filter(e -> e.headerId() == StandardSipHeaders.TO)
        .findAny()
        .map(SipHeaderLine::headerValues)
        .map(NameAddrParser::parse)
        .flatMap(NameAddr::getTag);
  }

  /**
   * returns the first Via header field value, if one exists.
   *
   * if the Via header field value exists but is not valid,
   * {@link ParseFailureException} is thrown.
   *
   * the Parser will stop after the first field value, so no guaruntee is made
   * that the entire
   * content of the field is valid.
   *
   * @param headerLines
   * @return
   *
   * @throws ParseFailureException
   *                               if the first Via field could not be parsed.
   */

  public static Optional<Via> topVia(final List<SipHeaderLine> headerLines) {
    return headerLines
        .stream()
        .filter(hdr -> hdr.knownHeaderId().orElse(null) == StandardSipHeaders.VIA)
        .map(SipHeaderLine::headerValues)
        .findFirst()
        .map(ViaParser.INSTANCE::parseFirstValue);
  }

  public static Optional<CSeq> cseq(List<SipHeaderLine> headerLines) {
    return headerLines.stream()
        .filter(e -> e.headerId() == StandardSipHeaders.CSEQ)
        .findAny()
        .map(SipHeaderLine::headerValues)
        .map(CSeqParser.INSTANCE::parseValue);
  }

  public static record TopViaRemovalResult(Optional<Via> topVia, List<SipHeaderLine> headers) {
  }

  public static TopViaRemovalResult removeTopViaHeader(List<SipHeaderLine> headerLines) {

    int topViaIndex = -1;

    for (int i = 0; i < headerLines.size(); i++) {
      if (headerLines.get(i).headerId() == StandardSipHeaders.VIA) {
        topViaIndex = i;
        break;
      }
    }

    if (topViaIndex == -1) {
      // there is no Via
      return new TopViaRemovalResult(Optional.empty(), headerLines);
    }

    String topViaValue = headerLines.get(topViaIndex).headerValues();

    final ParserInput input = ByteParserInput.fromString(topViaValue);
    final ParserContext ctx = new DefaultParserContext(input);

    Via topVia = ctx.read(ViaParser.INSTANCE);

    if (ctx.skip(ParserUtils.COMMA)) {

      // we have more ... so we need to modify this header line to include all except
      // the first one we just extracted.

      List<SipHeaderLine> mutated = new ArrayList<>(headerLines);

      CharSequence remaining = ctx.subSequence(ctx.position(), ctx.length());
      mutated.set(topViaIndex, StandardSipHeaders.VIA.ofLine(remaining.toString()));

      return new TopViaRemovalResult(Optional.of(topVia), mutated);

    } else if (ctx.remaining() > 0) {

      // more bytes, but we didn't have a COMMA so this is broken value.
      throw new ParseFailureException("invalid top Via header value");

    } else {

      // no more, so we can jsut remove the first value
      List<SipHeaderLine> mutated = new ArrayList<>(headerLines);
      mutated.remove(topViaIndex);
      return new TopViaRemovalResult(Optional.of(topVia), mutated);

    }

  }

}
