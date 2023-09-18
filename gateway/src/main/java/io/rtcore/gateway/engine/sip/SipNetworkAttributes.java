package io.rtcore.gateway.engine.sip;

import io.rtcore.gateway.udp.SipDatagramSocket;
import io.rtcore.sip.channels.api.SipAttributes;

public final class SipNetworkAttributes {

  /**
   * if received over a datagram socket, the socket which it was received from.
   */

  public static final SipAttributes.Key<SipDatagramSocket> ATTR_DATAGRAM_SOCKET = SipAttributes.Key.create("datagram-socket");

}
