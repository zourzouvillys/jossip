package io.rtcore.sip.message.processor.uri;

import io.rtcore.sip.common.Host;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.message.uri.SipUriVisitor;
import io.rtcore.sip.message.uri.TelUri;
import io.rtcore.sip.message.uri.TelUriVisitor;
import io.rtcore.sip.message.uri.Uri;

public class ToSipUserPhoneVisitor implements SipUriVisitor<SipUri>, TelUriVisitor<SipUri> {

  private final Host host;

  public ToSipUserPhoneVisitor(Host host) {
    this.host = host;
  }

  @Override
  public SipUri visit(final Uri unknown) {
    throw new IllegalArgumentException(unknown.toString());
  }

  @Override
  public SipUri visit(final SipUri uri) {
    return uri;
  }

  @Override
  public SipUri visit(final TelUri uri) {
    return SipUri.fromTelUri(uri, host);
  }

  public static ToSipUserPhoneVisitor toUri(Host host) {
    return new ToSipUserPhoneVisitor(host);
  }

}
