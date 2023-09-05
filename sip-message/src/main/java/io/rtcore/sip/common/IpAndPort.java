package io.rtcore.sip.common;

import java.net.InetSocketAddress;

import org.immutables.value.Value;

import com.google.common.net.InetAddresses;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public abstract class IpAndPort {

  protected IpAndPort() {
  }

  @Value.Parameter
  public abstract IpHost inetAddress();

  @Value.Parameter
  public abstract int port();

  public InetSocketAddress toSocketAddress() {
    return new InetSocketAddress(this.inetAddress().inetAddress(), this.port());
  }

  public static IpAndPort fromParts(final String address, final int port) {
    return ImmutableIpAndPort.of(IpHost.of(address), port);
  }

  public static IpAndPort of(final InetSocketAddress sa) {
    return ImmutableIpAndPort.of(IpHost.of(InetAddresses.toAddrString(sa.getAddress())), sa.getPort());
  }

  @Override
  public String toString() {
    return String.format("%s:%s", this.inetAddress().toUriString(), this.port());
  }

}
