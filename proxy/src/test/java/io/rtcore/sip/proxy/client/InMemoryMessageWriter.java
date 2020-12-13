package io.rtcore.sip.proxy.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import io.rtcore.sip.proxy.MessageWriter;
import io.rtcore.sip.proxy.chronicle.TransportEvent;

public class InMemoryMessageWriter implements MessageWriter {

  @Override
  public void write(InetSocketAddress source, InetSocketAddress target, ByteBuffer payload) {
  }

  @Override
  public void writeEvent(InetSocketAddress inetSocketAddress, InetSocketAddress remote, TransportEvent disconnect) {
  }

}
