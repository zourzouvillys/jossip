package io.rtcore.sip.sigcore.txn;

import java.net.SocketAddress;

/**
 * an incoming frame (logical packet).
 * 
 * these could be SIP, ICMP, or STUN, ICE, etc.
 * 
 * @author theo
 */

public interface RxFrame {

  /**
   * the source of the frame.
   */

  SocketAddress source();

  /**
   * the target of the frame.
   */

  SocketAddress target();

}
