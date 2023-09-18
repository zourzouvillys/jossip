package io.rtcore.gateway.udp;

import java.net.InetSocketAddress;

import org.immutables.value.Value;

@Value.Immutable
public interface SipDatagramSocketConfig {

  SipDatagramMessageHandler messageHandler();

  InetSocketAddress bindAddress();

}
