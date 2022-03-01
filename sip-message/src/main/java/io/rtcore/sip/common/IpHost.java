package io.rtcore.sip.common;

import java.net.InetAddress;

import org.immutables.value.Value;

import com.google.common.net.InetAddresses;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface IpHost extends Host {

  @Value.Parameter
  InetAddress inetAddress();

  @Value.Lazy
  @Override
  default String toUriString() {
    return InetAddresses.toUriString(this.inetAddress());
  }

  static IpHost of(String address) {
    return ImmutableIpHost.of(InetAddresses.forString(address));
  }

}
