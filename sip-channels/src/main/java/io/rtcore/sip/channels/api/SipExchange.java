package io.rtcore.sip.channels.api;

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
