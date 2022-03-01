package io.rtcore.sip.channels.netty.codec;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import com.google.common.base.CharMatcher;
import com.google.common.primitives.UnsignedInts;

import io.netty.handler.codec.DecoderException;
import io.rtcore.sip.common.ImmutableRequestLine;
import io.rtcore.sip.common.ImmutableResponseLine;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.SipStatusCodes;

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

}
