package com.jive.sip.transport.udp;

import java.net.InetSocketAddress;
import java.util.Objects;

import com.google.common.net.HostAndPort;
import com.jive.sip.transport.api.FlowId;

import lombok.ToString;

/**
 * {@link FlowId} implementation which references a local {@link ListenerId} and remote address over UDP.
 * 
 * @author theo
 * 
 */

@ToString
public class UdpFlowId implements FlowId
{

  private static final long serialVersionUID = 1L;

  private final HostAndPort remote;
  private final ListenerId lid;

  private UdpFlowId(final ListenerId lid, final HostAndPort remote)
  {
    this.lid = lid;
    this.remote = remote;
  }

  public static UdpFlowId create(final ListenerId lid, final String host, int port)
  {
    return new UdpFlowId(lid, HostAndPort.fromParts(host, port));
  }

  public static UdpFlowId create(final ListenerId lid, final String addressAndOptionalPort)
  {
    return new UdpFlowId(lid, HostAndPort.fromString(addressAndOptionalPort));
  }

  public static UdpFlowId create(final ListenerId lid, final HostAndPort remote)
  {
    return new UdpFlowId(lid, remote.withDefaultPort(5060));
  }

  public static UdpFlowId create(final ListenerId lid, final InetSocketAddress remote)
  {
    return new UdpFlowId(lid, HostAndPort.fromParts(remote.getAddress().getHostAddress(), remote.getPort()));
  }

  @Override
  public ListenerId getListenerId()
  {
    return this.lid;
  }

  @Override
  public HostAndPort getRemote()
  {
    return this.remote;
  }


  @Override
  public boolean equals(final Object o)
  {
    if (o instanceof UdpFlowId)
    {
      final UdpFlowId e = (UdpFlowId) o;
      return this.getRemote().equals(e.getRemote()) && (this.getListenerId().getListenerId() == e.getListenerId().getListenerId());
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(this.getRemote(), this.getListenerId());
  }

}
