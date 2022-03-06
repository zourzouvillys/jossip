package io.rtcore.resolver.dns;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;

import org.immutables.value.Value;

import com.google.common.base.Preconditions;
import com.google.common.net.InetAddresses;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public abstract class ClientSubnet {

  @Value.Parameter
  public abstract InetAddress address();

  @Value.Parameter
  @Value.Default
  public int mask() {
    return 0;
  }

  @Value.Check
  protected void check() {
    Preconditions.checkState(mask() >= 0, "'mask' must be >= 0");
    if (address() instanceof Inet4Address)
      Preconditions.checkState(mask() <= 32, "'mask' must be <= 32");
    else if (address() instanceof Inet6Address)
      Preconditions.checkState(mask() <= 128, "'mask' must be <= 128");
    else
      throw new IllegalStateException("address must be v4 or v6");
  }

  public String toString() {
    return String.format("%s/%d", InetAddresses.toAddrString(address()), mask());
  }

  public static ClientSubnet forAddress(String address) {
    return forNetwork(address, 0);
  }

  public static ClientSubnet forNetwork(String address, int mask) {
    return ImmutableClientSubnet.of(InetAddresses.forString(address), mask);
  }

}
