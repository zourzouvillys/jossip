package io.rtcore.sip.channels;

import java.net.URI;
import java.net.URISyntaxException;

public final class SipChannels {

  /**
   * create a {@link SipChannelOLD} builder from a target and channel credentials.
   *
   * <p>
   * A {@code NameResolver}-compliant URI is an absolute hierarchical URI as defined by
   * {@link java.net.URI}.
   *
   * Example URIs:
   *
   * <ul>
   * <li>{@code "sip:host.example.com:5061"}</li>
   * <li>{@code "sip:host.example.com;transport=tls"}</li>
   * <li>{@code "sip:host.example.com;transport=udp"}</li>
   * <li>{@code "sip:host.example.com;transport=ws"}</li>
   * <li>{@code "sip:host.example.com;transport=wss"}</li>
   * <li>{@code "dns:///%5B2001:db8:85a3:8d3:1319:8a2e:370:7348%5D:5061"}</li>
   * <li>{@code "dns://8.8.8.8/host.example.com:8080"}</li>
   * <li>{@code "dns://8.8.8.8/host.example.com"}</li>
   * <li>{@code "aws+xyz://xxx/xyz"}</li>
   * </ul>
   *
   */

  public static ManagedSipChannelBuilder<?> newChannelBuilder(final String target, final SipChannelCredentials creds) {
    throw new UnsupportedOperationException();
  }

  /**
   * create a new channel builder using the highest priority name resolver.
   */

  public static ManagedSipChannelBuilder<?> newChannelBuilder(final String host, final int port, final SipChannelCredentials creds) {
    return newChannelBuilder(authorityFromHostAndPort(host, port), creds);
  }

  /**
   *
   */

  private static String authorityFromHostAndPort(final String host, final int port) {
    try {
      return new URI(null, null, host, port, null, null, null).getAuthority();
    }
    catch (final URISyntaxException e) {
      throw new IllegalArgumentException("Invalid host or port: " + host + " " + port, e);
    }
  }

  /**
   * Static factory for creating a new ManagedSipUdpSocketBuilder.
   */

  public static ManagedSipUdpSocketBuilder<?> newUdpSocketBuilder() {
    return ManagedSipUdpSocketRegistry.getDefaultRegistry().newBuilder();
  }

}
