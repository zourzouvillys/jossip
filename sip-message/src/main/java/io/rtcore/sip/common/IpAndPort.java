package io.rtcore.sip.common;

import java.net.InetSocketAddress;

import org.immutables.value.Value;

import com.google.common.net.InetAddresses;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public abstract class IpAndPort {

  @Value.Parameter
  public abstract IpHost inetAddress();

  @Value.Parameter
  public abstract int port();

  public InetSocketAddress toSocketAddress() {
    return new InetSocketAddress(this.inetAddress().inetAddress(), port());
  }

  public static IpAndPort fromParts(String address, int port) {
    return ImmutableIpAndPort.of(IpHost.of(address), port);
  }

  public static IpAndPort of(InetSocketAddress sa) {
    return ImmutableIpAndPort.of(IpHost.of(InetAddresses.toAddrString(sa.getAddress())), sa.getPort());
  }

  public String toString() {
    return String.format("%s:%s", inetAddress().toUriString(), port());
  }

}
