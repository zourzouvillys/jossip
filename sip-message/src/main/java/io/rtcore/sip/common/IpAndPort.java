package io.rtcore.sip.common;

import java.net.InetSocketAddress;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface IpAndPort {

  @Value.Parameter
  IpHost inetAddress();

  @Value.Parameter
  int port();

  default InetSocketAddress toSocketAddress() {
    return new InetSocketAddress(this.inetAddress().inetAddress(), port());
  }

  static IpAndPort fromParts(String address, int port) {
    return ImmutableIpAndPort.of(IpHost.of(address), port);
  }

}
