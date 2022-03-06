package io.rtcore.sip.channels.api;

public interface SipChannel {

  /**
   * an exchange of a SIP request over this channel.
   */

  SipClientExchange exchange(SipRequestFrame req);

}
