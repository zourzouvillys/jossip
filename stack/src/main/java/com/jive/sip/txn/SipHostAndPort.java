package com.jive.sip.txn;

import java.net.InetAddress;
import java.net.InetSocketAddress;

import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.jive.sip.message.api.SipTransport;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The tuple used in a Via header and other places. Will always have an associated transport.
 *
 * @author theo
 */

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class SipHostAndPort
{

  /**
   * The hostname or IP address.
   */

  private final String hostname;

  /**
   * The port number.
   *
   * There is a distinction between a SIP-URI (via sent-by) with a port and without. The port may be specified even when it's the default to
   * indicate SRC/NAPTR should not be used to resolve the target.
   *
   * Because of this, we store it the same here. Do not store the default port if one is not provided, and instead chose on retreiving if it
   * should have one.
   *
   */

  private final Integer port;

  /**
   * The SIP transport.
   */

  private final SipTransport transport;

  public static SipHostAndPort tls(final String hostname)
  {
    return new SipHostAndPort(hostname.toLowerCase(), null, SipTransport.TLS);
  }

  public static SipHostAndPort tls(final String hostname, final int port)
  {
    return new SipHostAndPort(hostname.toLowerCase(), port, SipTransport.TLS);
  }

  public static SipHostAndPort tcp(final String hostname)
  {
    return new SipHostAndPort(hostname.toLowerCase(), null, SipTransport.TCP);
  }

  public static SipHostAndPort tcp(final String hostname, final int port)
  {
    return new SipHostAndPort(hostname.toLowerCase(), port, SipTransport.TCP);
  }

  public static SipHostAndPort udp(final String hostname)
  {
    return new SipHostAndPort(hostname.toLowerCase(), null, SipTransport.UDP);
  }

  public static SipHostAndPort udp(final String hostname, final int port)
  {
    return new SipHostAndPort(hostname.toLowerCase(), port, SipTransport.UDP);
  }

  public static SipHostAndPort udp(final InetAddress addr, final int port)
  {
    return new SipHostAndPort(addr.getHostAddress(), port, SipTransport.UDP);
  }

  public static SipHostAndPort udp(final InetSocketAddress addr)
  {
    return udp(addr.getAddress(), addr.getPort());
  }

  /**
   * is this an IP address? false if a hostname.
   *
   * @return
   */

  public boolean isLiteral()
  {
    return InetAddresses.isInetAddress(this.hostname);
  }

  @Override
  public String toString()
  {
    final StringBuilder sb = new StringBuilder();

    sb.append(this.hostname);

    if (this.port != null)
    {
      sb.append(':').append(this.port);
    }
    sb.append('/').append(this.transport);

    return sb.toString();
  }

  public int getPortOrDefault()
  {
    if (this.port != null)
    {
      return this.port;
    }
    if (this.transport == SipTransport.UDP)
    {
      return 5060;
    }
    else if (this.transport == SipTransport.TCP)
    {
      return 5060;
    }
    else if (this.transport == SipTransport.TLS)
    {
      return 5061;
    }
    else
    {
      throw new RuntimeException("Transport not supported");
    }
  }

  public InetSocketAddress toSockAddr()
  {
    return new InetSocketAddress(InetAddresses.forString(this.hostname), this.getPortOrDefault());
  }

  public HostAndPort toHostAndPort()
  {
    if (this.port != null)
    {
      return HostAndPort.fromParts(this.hostname, this.getPort());
    }
    return HostAndPort.fromString(this.hostname);
  }

}
