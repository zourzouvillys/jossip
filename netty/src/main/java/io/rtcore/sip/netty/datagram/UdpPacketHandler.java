package io.rtcore.sip.netty.datagram;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@FunctionalInterface
public interface UdpPacketHandler {

  void acceptUdpPacket(InetSocketAddress local, InetSocketAddress remote, ByteBuffer buffer);

}
