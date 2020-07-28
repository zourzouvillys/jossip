package io.rtcore.dns.monitor;

import org.immutables.value.Value;

import com.google.common.net.HostAndPort;
import com.google.common.net.InternetDomainName;
import com.google.common.primitives.UnsignedInts;

@Value.Immutable
public interface SrvRecord extends Record {

  // [priority] [weight] [port] [server host name]

  int priority();

  int weight();

  HostAndPort server();

  static SrvRecord parse(String value) {
    String[] parts = value.split(" ", 4);
    if (parts.length != 4) {
      return null;
    }
    return ImmutableSrvRecord.builder()
      .priority(UnsignedInts.parseUnsignedInt(parts[0]))
      .weight(UnsignedInts.parseUnsignedInt(parts[1]))
      .server(HostAndPort.fromParts(InternetDomainName.from(parts[3]).toString(), UnsignedInts.parseUnsignedInt(parts[2])))
      .build();
  }

}
