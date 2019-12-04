package com.jive.sip.processor.uri;

import com.jive.sip.uri.api.SipUri;
import com.jive.sip.uri.api.SipUriVisitor;
import com.jive.sip.uri.api.Uri;

public class SipUriExtractor implements SipUriVisitor<SipUri>
{

  private static final SipUriExtractor INSTANCE = new SipUriExtractor();

  @Override
  public SipUri visit(final Uri unknown)
  {
    return null;
  }

  @Override
  public SipUri visit(final SipUri uri)
  {
    return uri;
  }

  public static SipUriExtractor getInstance()
  {
    return INSTANCE;
  }

}
