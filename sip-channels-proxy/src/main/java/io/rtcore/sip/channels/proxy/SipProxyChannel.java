package io.rtcore.sip.channels.proxy;

import io.rtcore.sip.channels.api.SipChannel;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.frame.SipRequestFrame;

/**
 * a SIP channel which will proxy incoming exchange requests, to downstream instances based on
 * rules.
 */

public class SipProxyChannel implements SipChannel {

  private final SipProxyChannelProvider provider;

  public SipProxyChannel(SipProxyChannelProvider provider) {
    this.provider = provider;
  }

  /**
   * fetch the next channel to send the SIP request over.
   */

  @Override
  public SipClientExchange exchange(SipRequestFrame req) {
    return provider.nextChannel(req).exchange(req);
  }

}
