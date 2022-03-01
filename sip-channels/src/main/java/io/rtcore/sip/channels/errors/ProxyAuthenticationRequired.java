package io.rtcore.sip.channels.errors;

import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;

public class ProxyAuthenticationRequired extends ClientFailure {

  private static final long serialVersionUID = 1L;

  public ProxyAuthenticationRequired(SipResponseStatus status) {
    super(status);
  }

  public ProxyAuthenticationRequired(SipResponse res) {
    super(res);
  }

}
