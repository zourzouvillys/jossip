package com.jive.sip.processor.uri;

import java.util.Optional;

import com.jive.sip.parsers.core.ByteParserInput;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.processor.uri.parsers.TelUriParser;
import com.jive.sip.uri.api.SipUri;
import com.jive.sip.uri.api.SipUriVisitor;
import com.jive.sip.uri.api.TelUri;
import com.jive.sip.uri.api.TelUriVisitor;
import com.jive.sip.uri.api.Uri;

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
