package com.jive.sip.transport.tcp;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.processor.rfc3261.RfcSipMessageManagerBuilder;
import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.transport.udp.DispatchingEventListener;
import com.jive.sip.transport.udp.ListenerId;

/**
 * Handles incoming and outgoing TCP connections.
 * 
 * Keeps track of the open connections, and handles requesting of new outgoing connections.
 * 
 */

@Slf4j
public class TcpTransportManager
{

  private final Map<ListenerId, TcpListener> listeners = Maps.newHashMap();
  private final SipMessageManager manager = new RfcSipMessageManagerBuilder().build();
  private final DispatchingEventListener<TcpTransportListener> dispatchers = DispatchingEventListener.create(TcpTransportListener.class);
  private final EventLoopGroup group = new NioEventLoopGroup(1, new ThreadFactoryBuilder().setNameFormat("tcp-%s").build());


  public TcpTransportManager()
  {
  }

  /**
   * Adds a new listener bound to the given address/port.
   * 
   * @param lid
   *          The listener ID to use to reference this listener.
   * 
   * @param addr
   *          The address to bind to.
   * 
   */

  public void addListener(final ListenerId lid, final InetSocketAddress addr, final TcpConnectionFactory factory)
  {
    log.info("Adding TCP listener {} to {} with handler {}", lid, addr);
    final TcpListener listener = new TcpListener(this, this.group, lid, factory);
    listener.bind(addr);
    this.listeners.put(lid, listener);
  }

  public SipMessageManager getSipMessageManager()
  {
    return this.manager;
  }

  public TcpTransportListener getInvoker()
  {
    return this.dispatchers.getInvoker();
  }

  public void addListener(final TcpTransportListener listener, final Executor executor)
  {
    this.dispatchers.add(listener, executor);
  }

  public void send(final TcpFlowId flowId, final SipResponse msg)
  {

    log.debug("Sending to {}: {}", flowId, msg);

    final TcpChannel ch = this.sockets.get(flowId.getIndex());

    if (ch != null)
    {
      ch.writeAndFlush(msg);
    }
    else
    {
      log.warn("Attempted to send to terminated TCP flow: {}", flowId);
    }
  }

  private final AtomicLong index = new AtomicLong(0);
  private final Map<Long, TcpChannel> sockets = Maps.newHashMap();
  private final Lock lock = new ReentrantLock();

  public TcpFlowId create(final TcpChannel ch, final ListenerId listenerId, final HostAndPort remote)
  {
    this.lock.lock();
    try
    {
      final long id = this.index.incrementAndGet();
      this.sockets.put(id, ch);
      log.debug("Added {}, count now {}", remote, this.sockets.size());
      return TcpFlowId.create(listenerId, id, remote);
    }
    finally
    {
      this.lock.unlock();
    }
  }

  public void close(final TcpFlowId flowId)
  {
    this.lock.lock();
    try
    {
      this.sockets.remove(flowId.getIndex());
      log.debug("Removed socket, count now {}", this.sockets.size());
    }
    finally
    {
      this.lock.unlock();
    }
  }

}
