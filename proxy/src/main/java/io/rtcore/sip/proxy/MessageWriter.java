package io.rtcore.sip.proxy;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import io.rtcore.sip.proxy.chronicle.TransportEvent;

public interface MessageWriter {

  void write(InetSocketAddress source, InetSocketAddress target, ByteBuffer payload);

  void writeEvent(InetSocketAddress inetSocketAddress, InetSocketAddress remote, TransportEvent disconnect);

}
