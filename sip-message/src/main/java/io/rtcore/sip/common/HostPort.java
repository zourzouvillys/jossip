package io.rtcore.sip.common;

import java.util.OptionalInt;

import org.immutables.value.Value;

import com.google.common.net.HostAndPort;

/**
 * provides guarunteed normalized host and optional port.
 *
 * hosts will be normalized per InternetDomainName.
 *
 * port - if any, will always be > 0.
 *
 * @author theo
 *
 */

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface HostPort {

  @Value.Parameter
  Host host();

  @Value.Parameter
  OptionalInt port();

  @Value.Lazy
  default String toUriString() {
    if (this.port().isEmpty()) {
      return this.host().toUriString();
    }
    return this.host().toUriString() + ":" + this.port().getAsInt();
  }

  static HostPort of(final Host host) {
    return ImmutableHostPort.of(host, OptionalInt.empty());
  }

  static HostPort of(final Host host, final int port) {
    return ImmutableHostPort.of(host, OptionalInt.of(port));
  }

  static HostPort of(final Host host, final OptionalInt port) {
    return ImmutableHostPort.of(host, port);
  }

  static HostPort fromHost(final String host) {
    return of(Host.fromString(host));
  }

  /**
   *
   * @param host
   * @param port
   *          if -1, same as no port.
   * @return
   */

  static HostPort fromParts(final String host, final int port) {
    if (port == -1) {
      return of(Host.fromString(host));
    }
    return of(Host.fromString(host), port);
  }

  static HostPort fromParts(final String host, final OptionalInt port) {
    return of(Host.fromString(host), port);
  }

  static HostPort fromString(final String host) {
    // strip optional brackets from IPv6 literals.
    final HostAndPort parsedHost = HostAndPort.fromHost(host);
    return fromParts(parsedHost.getHost(), parsedHost.getPortOrDefault(-1));
  }
}
