package io.rtcore.sip.proxy.transport.stream;

import java.net.InetSocketAddress;
import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public interface TcpInfo {

  InetSocketAddress local();

  InetSocketAddress remote();

  Optional<TlsInfo> tls();

}
