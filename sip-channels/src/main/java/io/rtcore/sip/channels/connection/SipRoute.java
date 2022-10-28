package io.rtcore.sip.channels.connection;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.immutables.value.Value;

import io.rtcore.sip.common.DnsHost;
import io.rtcore.sip.common.iana.StandardSipTransportName;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface SipRoute {

  /**
   * the transport layer protocol to use (e.g, TLS, TCP, WSS, etc).
   */

  StandardSipTransportName transportProtocol();

  /**
   * the serverName to provide in TLS SNI (if any).
   * 
   * if this is provided, then a connection can not be reused unless the value matches.
   * 
   */

  Optional<DnsHost> remoteAuthority();

  /**
   * the remote transport address. this value must be an address, not an unresolved host name.
   * 
   */

  InetSocketAddress remoteAddress();

  /**
   * the local address for the connection. If the port is 0, a random local port will be assigned by
   * the operating system. this value must be an address, not an unresolved host name.
   */

  Optional<InetSocketAddress> localAddress();

  /**
   * the set of DNS names which would be acceptable for the remote identity. note that even if a
   * remote authority is provided, the same value must be included here if it can be used as a
   * common name or subjectAltName.
   */

  Set<String> remoteServerNames();

  /**
   * the set of SOCKS proxies to traverse to get to the remote address. these values must be an
   * address, not an unresolved host name.
   */

  List<InetSocketAddress> proxyChain();

  /**
   * 
   */

  static ImmutableSipRoute tcp(InetAddress addr) {
    return ImmutableSipRoute.builder()
      .transportProtocol(StandardSipTransportName.TCP)
      .remoteAddress(new InetSocketAddress(addr, 5060))
      .build();
  }

  static ImmutableSipRoute tcp(InetAddress addr, int port) {
    return ImmutableSipRoute.builder()
      .transportProtocol(StandardSipTransportName.TCP)
      .remoteAddress(new InetSocketAddress(addr, port))
      .build();
  }

}
