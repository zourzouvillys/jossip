package io.rtcore.sip.channels.netty.codec;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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

public class SipParsingUtils {

  private static final CharMatcher TOKEN_CHAR = CharMatcher.anyOf("-.!%*_+`'~abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");
  private static final CharMatcher DIGIT_CHAR = CharMatcher.inRange('0', '9');

  public static SipInitialLine parseInitialLine(String initialLine) {

    if (initialLine.endsWith(" SIP/2.0") && initialLine.length() >= 12) { // 12 == min possible, 'A
                                                                          // s: SIP/2.0'

      initialLine = initialLine.substring(0, initialLine.length() - " SIP/2.0".length());

      int idx = initialLine.indexOf(' ');

      if (idx == -1) {
        throw new DecoderException("invalid initial SIP/2.0 line");
      }

      String methodToken = initialLine.substring(0, idx);

      if (!TOKEN_CHAR.matchesAllOf(methodToken)) {
        throw new DecoderException("invalid chars in sip method");
      }

      SipMethodId method = SipMethods.toMethodId(methodToken);

      try {
        URI uri = new URI(initialLine.substring(idx + 1, initialLine.length()));
        return ImmutableRequestLine.of(method, uri);
      }
      catch (URISyntaxException e) {
        throw new DecoderException("invalid r-uri");
      }

    }
    else if (initialLine.startsWith("SIP/2.0 ") && initialLine.length() >= 11) { // 11 == with just
                                                                                 // status code

      initialLine = initialLine.substring("SIP/2.0 ".length());

      String statusToken = initialLine.substring(0, 3);

      if (!DIGIT_CHAR.matchesAllOf(statusToken)) {
        throw new DecoderException("invalid status code");
      }

      int status = UnsignedInts.parseUnsignedInt(statusToken);

      if (status < 100 || status > 699) {
        throw new DecoderException("invalid status code");
      }

      if (initialLine.length() > 3 && initialLine.charAt(3) != ' ') {
        throw new DecoderException("missing space after initial response line status code");
      }

      Optional<String> reason =
        Optional.ofNullable(initialLine)
          .filter(line -> line.length() > 4)
          .map(line -> line.substring(4).trim())
          .filter(e -> !e.isBlank())
          .filter(line -> !Optional.ofNullable(SipStatusCodes.forStatusCode(status)).map(SipStatusCodes::reasonPhrase).orElse("").equals(line));

      return ImmutableResponseLine.of(status, reason);

    }
    else {

      // invalid.
      throw new DecoderException("invalid initial SIP/2.0 line");

    }

  }

  public static ArrayList<ImmutableSipHeaderLine> parseHeaders(ByteBuf buffer) {

    int maxHeaderBytes = 4096;
    int maxHeaderCount = 1024;

    AppendableCharSequence seq = new AppendableCharSequence(256);

    SipHeaderParser headerParser = new SipHeaderParser(seq, 4096);

    AppendableCharSequence line = headerParser.parse(buffer);

    if (line == null) {
      return null;
    }

    ArrayList<ImmutableSipHeaderLine> headers = new ArrayList<>();

    String name = null;
    String value = null;

    while (line.length() > 0) {

      char firstChar = line.charAtUnsafe(0);

      if (name != null && (firstChar == ' ' || firstChar == '\t')) {

        // this is LWS.

        // please do not make one line from below code
        // as it breaks +XX:OptimizeStringConcat optimization
        String trimmedLine = line.toString().trim();
        String valueStr = String.valueOf(value);
        value = valueStr + ' ' + trimmedLine;

        if (value.length() > maxHeaderBytes) {
          throw new TooLongFrameException("header line too long");
        }

      }
      else {

        if (name != null) {
          headers.add(ImmutableSipHeaderLine.of(name, value));
          name = null;
          value = null;
        }

        if (headers.size() >= maxHeaderCount) {
          // sanity, avoid too many SIP header lines.
          throw new TooLongFrameException("too many header lines");
        }

        String[] p = line.toString().split(":", 2);

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

    // we have a full set of headers, validate and check to see if we are expecting content.
    // processHeaders(ctx, buffer, out);

    // this.headerParser.reset();

    return headers;

  }

  public static OptionalInt readContentLength(ArrayList<ImmutableSipHeaderLine> headers) {

    Set<String> contentLengths =
      headers.stream()
        .filter(e -> e.knownHeaderId().filter(id -> id == StandardSipHeaders.CONTENT_LENGTH).isPresent())
        .map(SipHeaderLine::headerValues)
        .collect(Collectors.toSet());

    if (contentLengths.isEmpty()) {

      return OptionalInt.empty();

    }
    else if (contentLengths.size() == 1) {

      String firstField = contentLengths.iterator().next();

      if (!Character.isDigit(firstField.charAt(0))) {
        throw new CorruptedFrameException("content-length value is not a number: " + firstField);
      }

      final int value = Integer.parseUnsignedInt(firstField);

      if (value < 0) {
        throw new CorruptedFrameException("invalid content-length value: " + value);

      }
      return OptionalInt.of(value);

    }
    else {

      return OptionalInt.empty();

    }

  }

}
