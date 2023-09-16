package io.rtcore.sip.channels.api;

import io.rtcore.sip.frame.SipRequestFrame;

public interface SipChannel {

  /**
   * an exchange of a SIP request over this channel.
   */

  SipClientExchange exchange(SipRequestFrame req);

}
