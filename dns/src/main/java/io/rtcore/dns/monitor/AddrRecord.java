package io.rtcore.dns.monitor;

import java.net.InetAddress;

import org.immutables.value.Value;

import com.google.common.net.InetAddresses;

@Value.Immutable
public interface AddrRecord extends Record {

  InetAddress address();

  static AddrRecord parse(String value) {
    return ImmutableAddrRecord.builder().address(InetAddresses.forString(value)).build();
  }

}
