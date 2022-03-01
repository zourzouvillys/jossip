package io.rtcore.sip.channels.netty.tcp;

import io.rtcore.sip.channels.netty.codec.SipRequestFrame;

public interface SipExchange {

  /**
   * the request which is being sent over this connection.
   */

  SipRequestFrame request();

  /**
   * the SipConnection which this request was sent over.
   */

  SipConnection connection();

}
