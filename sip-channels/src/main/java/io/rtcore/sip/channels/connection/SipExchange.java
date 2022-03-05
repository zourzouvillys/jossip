package io.rtcore.sip.channels.connection;

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
