package io.rtcore.sip.channels.netty.udp;

import java.net.InetSocketAddress;

import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.socket.DatagramChannel;
import io.rtcore.sip.channels.api.SipFrame;

/**
 * The message container that is used for {@link DatagramChannel} to communicate with the remote
 * peer.
 */
public class SipDatagramPacket extends DefaultAddressedEnvelope<SipFrame, InetSocketAddress> {

  /**
   * Create a new instance with the specified packet {@code data} and {@code recipient} address.
   */
  public SipDatagramPacket(SipFrame data, InetSocketAddress recipient) {
    super(data, recipient);
  }

  /**
   * Create a new instance with the specified packet {@code data}, {@code recipient} address, and
   * {@code sender} address.
   */

  public SipDatagramPacket(SipFrame data, InetSocketAddress recipient, InetSocketAddress sender) {
    super(data, recipient, sender);
  }

}
