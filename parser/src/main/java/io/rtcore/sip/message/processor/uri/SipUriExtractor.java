package io.rtcore.sip.message.processor.uri;

import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.message.uri.SipUriVisitor;
import io.rtcore.sip.message.uri.Uri;

public class SipUriExtractor implements SipUriVisitor<SipUri> {

  private static final SipUriExtractor INSTANCE = new SipUriExtractor();

  @Override
  public SipUri visit(final Uri unknown) {
    return null;
  }

  @Override
  public SipUri visit(final SipUri uri) {
    return uri;
  }

  public static SipUriExtractor getInstance() {
    return INSTANCE;
  }

}
