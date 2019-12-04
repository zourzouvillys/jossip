package com.jive.sip.transport.udp;

import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;

public interface UdpTransportListener
{

  void onSipRequestReceived(final UdpFlowId flow, final InetSocketAddress sender, final SipRequest msg);

  void onSipResponseReceived(final UdpFlowId flow, final InetSocketAddress sender, final SipResponse msg);

  void onInvalidSipMessageEvent(final UdpFlowId createFlowId, final InetSocketAddress sender);

  void onKeepalive(final UdpFlowId flow, final InetSocketAddress sender);

  void onStunPacket(final UdpFlowId flow, final DatagramPacket pkt);

}
