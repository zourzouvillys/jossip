package io.rtcore.sip.proxy.transport.datagram;

import java.net.InetSocketAddress;

public interface DatagramListenerSpec {

  InetSocketAddress bindAddress();

  InetSocketAddress externalAddress();

}
