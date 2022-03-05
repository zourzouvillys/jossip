package io.rtcore.sip.channels.proxy;

import io.rtcore.sip.channels.connection.SipRequestFrame;

public class SipProxy {

  /**
   * create a proxy context for a SIP request.
   */

  public SipProxyContext createProxyContext(SipRequestFrame frame) {
    return new SipProxyContext(this);
  }

}
