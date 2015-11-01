package com.jive.sip.transport.tcp;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.BaseEncoding;
import com.google.common.net.HostAndPort;
import com.jive.sip.transport.api.FlowId;
import com.jive.sip.transport.udp.ListenerId;

import lombok.ToString;

/**
 * {@link FlowId} implementation which references a local {@link ListenerId} and remote address over UDP.
 * 
 * @author theo
 * 
 */

@ToString
public class TcpFlowId implements FlowId
{

  private static final long serialVersionUID = 1L;

  private final ListenerId listenerId;
  private final long id;
  private final HostAndPort remote;

  private TcpFlowId(final ListenerId listenerId, final long lid, final HostAndPort remote)
  {
    this.listenerId = listenerId;
    this.id = lid;
    this.remote = remote;
  }

  public static TcpFlowId create(final ListenerId listenerId, final long lid, final HostAndPort remote)
  {
    return new TcpFlowId(listenerId, lid, remote);
  }

  public long getIndex()
  {
    return this.id;
  }

  /**
   * Note: locally initiated TCP connections may not have a listener, if it was initiated outbound without an associated
   * listener.
   */

  @Override
  public ListenerId getListenerId()
  {
    return this.listenerId;
  }


  @Override
  public boolean equals(final Object o)
  {
    if (o instanceof TcpFlowId)
    {
      final TcpFlowId e = (TcpFlowId) o;
      return this.getRemote().equals(e.getRemote()) && (this.getListenerId().getListenerId() == e.getListenerId().getListenerId()) && (this.getIndex() == e.getIndex());
    }
    return false;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(this.getRemote(), this.getListenerId(), this.getIndex());
  }


  @Override
  public HostAndPort getRemote()
  {
    return this.remote;
  }

  public String encode()
  {

    final List<String> parts = Lists.newLinkedList();

    parts.add(Long.toString(this.id));
    parts.add(this.remote.toString());

    if (this.listenerId != null)
    {
      parts.add(Integer.toString(this.listenerId.getListenerId()));
    }

    return BaseEncoding.base64Url().omitPadding().encode(Joiner.on('|').join(parts).getBytes(Charsets.UTF_8));

  }

  public static TcpFlowId decode(final String token)
  {

    final String data = new String(BaseEncoding.base64Url().omitPadding().decode(token), Charsets.UTF_8);

    final Iterator<String> it = Splitter.on('|').split(data).iterator();

    final long lid = Long.parseLong(it.next());
    final HostAndPort remote = HostAndPort.fromString(it.next());

    final ListenerId listenerId;

    if (it.hasNext())
    {
      listenerId = new ListenerId(Integer.parseInt(it.next()));
    }
    else
    {
      listenerId = null;
    }

    return new TcpFlowId(listenerId, lid, remote);

  }

}
