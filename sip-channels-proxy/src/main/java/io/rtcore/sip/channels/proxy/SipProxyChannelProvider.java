package io.rtcore.sip.channels.proxy;

import io.rtcore.sip.channels.api.SipChannel;
import io.rtcore.sip.frame.SipRequestFrame;

public interface SipProxyChannelProvider {

  /**
   * provides the next channel to send a request to.
   * 
   * @param req
   */

  SipChannel nextChannel(SipRequestFrame req);

}
