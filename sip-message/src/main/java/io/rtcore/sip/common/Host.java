package io.rtcore.sip.common;

import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;

/**
 * A domain name, like google.com
 *
 * A IPv4 address string, like 127.0.0.1
 *
 * An IPv6 address string with or without brackets, like [2001:db8::1] or 2001:db8::1
 *
 */

public interface Host {

  String toUriString();

  String toAddrString();

  static Host fromString(String host) {

    // Verify that no port was specified, and strip optional brackets from
    // IPv6 literals.

    final HostAndPort parsedHost = HostAndPort.fromHost(host);
    Preconditions.checkArgument(!parsedHost.hasPort());
    host = parsedHost.getHost();

    // is it an IP address?
    if (InetAddresses.isInetAddress(host)) {
      return ImmutableIpHost.of(InetAddresses.forString(host));
    }

    // It is not any kind of IP address; must be a domain name, or invalid.

    return DnsHost.of(host);

  }

}
