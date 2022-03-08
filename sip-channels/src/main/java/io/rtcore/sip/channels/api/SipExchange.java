package io.rtcore.sip.channels.api;

import io.rtcore.sip.channels.internal.SipAttributes;

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
