package com.jive.sip.processor.uri;

import com.jive.sip.uri.SipUri;
import com.jive.sip.uri.SipUriVisitor;
import com.jive.sip.uri.TelUri;
import com.jive.sip.uri.TelUriVisitor;
import com.jive.sip.uri.Uri;

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
