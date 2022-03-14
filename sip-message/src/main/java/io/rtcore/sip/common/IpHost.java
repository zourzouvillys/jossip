package io.rtcore.sip.common;

import java.net.InetAddress;

import org.immutables.value.Value;

import com.google.common.net.InetAddresses;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public abstract class IpHost implements Host {

  @Value.Parameter
  public abstract InetAddress inetAddress();

  @Value.Lazy
  @Override
  public String toUriString() {
    return InetAddresses.toUriString(this.inetAddress());
  }

  @Value.Lazy
  @Override
  public String toAddrString() {
    return InetAddresses.toAddrString(this.inetAddress());
  }

  public static IpHost of(String address) {
    return ImmutableIpHost.of(InetAddresses.forString(address));
  }

}
