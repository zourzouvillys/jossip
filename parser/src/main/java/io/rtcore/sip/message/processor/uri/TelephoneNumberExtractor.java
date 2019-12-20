package io.rtcore.sip.message.processor.uri;

import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.message.uri.SipUriVisitor;
import io.rtcore.sip.message.uri.TelUri;
import io.rtcore.sip.message.uri.TelUriVisitor;
import io.rtcore.sip.message.uri.Uri;

public class TelephoneNumberExtractor implements SipUriVisitor<String>, TelUriVisitor<String> {

  private static final TelephoneNumberExtractor INSTANCE = new TelephoneNumberExtractor();

  @Override
  public String visit(final Uri unknown) {
    return null;
  }

  @Override
  public String visit(final SipUri uri) {
    return uri.getUsername().orElse(null);
  }

  @Override
  public String visit(final TelUri uri) {
    return uri.number();
  }

  public static TelephoneNumberExtractor getInstance() {
    return INSTANCE;
  }

}
