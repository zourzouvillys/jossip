package com.jive.sip.processor.uri;

import com.jive.sip.uri.api.SipUri;
import com.jive.sip.uri.api.SipUriVisitor;
import com.jive.sip.uri.api.TelUri;
import com.jive.sip.uri.api.TelUriVisitor;
import com.jive.sip.uri.api.Uri;

public class TelephoneNumberExtractor implements SipUriVisitor<String>, TelUriVisitor<String>
{

  private static final TelephoneNumberExtractor INSTANCE = new TelephoneNumberExtractor();

  @Override
  public String visit(final Uri unknown)
  {
    return null;
  }

  @Override
  public String visit(final SipUri uri)
  {
    return uri.getUsername().orElse(null);
  }

  @Override
  public String visit(final TelUri uri)
  {
    return uri.getNumber();
  }

  public static TelephoneNumberExtractor getInstance()
  {
    return INSTANCE;
  }

}
