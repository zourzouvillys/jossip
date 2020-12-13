package io.rtcore.sip.proxy.transport.stream.server;

import java.net.InetSocketAddress;

public interface StreamListenerSpec {

  InetSocketAddress bindAddress();

  InetSocketAddress externalAddress();

  boolean tls();

  boolean proxyProtocol();

}
