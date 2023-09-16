package io.rtcore.sip.channels.api;

import io.rtcore.sip.frame.SipRequestFrame;

public interface SipExchange {

  /**
   * the request which is being sent over this connection.
   */

  SipRequestFrame request();

  /**
   * attributes
   */

  SipAttributes attributes();

}
