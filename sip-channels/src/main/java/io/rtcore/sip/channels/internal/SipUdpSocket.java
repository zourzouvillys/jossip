package io.rtcore.sip.channels.internal;

import java.net.InetSocketAddress;
import java.util.concurrent.Flow.Publisher;

import io.rtcore.sip.message.message.SipMessage;

public interface SipUdpSocket extends SipWireIn {

  /**
   * return a subscriber which will write any provided messages to the specified target.
   *
   * NAT and keepalive logic may use the status of these subscribers to alter behavior. for example,
   * ensuring keepalives are sent for the duration of the subscriber being present. configuration
   * may disallow destinations that we have not seen a packet from in the past, e.g because we're
   * behind a load balancer which only allows incoming sessions.
   *
   * the subscriber may cancel or generate an error on send if the target is no longer reachable,
   * based on transport specific heuristics.
   *
   */

  void send(InetSocketAddress target, Publisher<SipMessage> msg);

}
