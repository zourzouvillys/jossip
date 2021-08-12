package io.rtcore.sip.message.processor.uri;

import java.util.Optional;

import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.processor.uri.parsers.TelUriParser;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.message.uri.SipUriVisitor;
import io.rtcore.sip.message.uri.TelUri;
import io.rtcore.sip.message.uri.TelUriVisitor;
import io.rtcore.sip.message.uri.Uri;

public class TelUriExtractor implements SipUriVisitor<TelUri>, TelUriVisitor<TelUri> {

  private static final TelUriExtractor INSTANCE = new TelUriExtractor();

  public static TelUriExtractor getInstance() {
    return INSTANCE;
  }

  @Override
  public TelUri visit(final Uri unknown) {
    return null;
  }

  @Override
  public TelUri visit(final TelUri uri) {
    return uri;
  }

  @Override
  public TelUri visit(final SipUri uri) {
    if (!uri.getUserParameter().equals(Optional.of("phone"))) {
      return null;
    }

    final String tel = uri.getUsername().orElse(null);

    if (tel == null) {
      return null;
    }

    final ByteParserInput is = ByteParserInput.fromString(tel);

    final TelUri value = ParserUtils.read(is, TelUriParser.TEL);

    if (is.remaining() == 0) {
      return value;
    }

    throw new RuntimeException(String.format("Invalid tel URI in SIP-URI: %s", tel));

  }

}
