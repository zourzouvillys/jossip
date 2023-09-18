package io.rtcore.gateway.udp;

import io.rtcore.sip.netty.codec.udp.SipDatagramPacket;

/**
 * This functional interface defines a contract for handling SIP datagram packets. Implementations
 * of this interface are responsible for processing incoming SIP datagram frames in a non-blocking
 * manner and should handle exceptions internally.
 */

@FunctionalInterface
public interface SipDatagramMessageHandler {

  /**
   * Receives and processes a SIP datagram packet.
   *
   * <p>
   * Implementations of this method should handle the incoming SIP datagram packet in a non-blocking
   * manner to ensure that it does not cause undue delays or block the calling thread. This may
   * involve parsing the packet, extracting SIP data, and performing any necessary processing. Error
   * handling should be performed internally within the implementation, and exceptions should not be
   * thrown.
   *
   * <p>
   * It is recommended that implementations log or report any errors encountered during packet
   * processing, depending on the application's requirements.
   *
   * @param socket
   *          The {@link SipDatagramSocket} which received the packet.
   *
   * @param packet
   *          The SIP datagram packet to be handled.
   */

  void receiveDatagramFrame(SipDatagramSocket socket, SipDatagramPacket packet);

}
