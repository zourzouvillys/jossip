package com.jive.sip.transport.udp;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.jive.sip.message.api.SipMessage;
import com.jive.sip.processor.rfc3261.RfcSipMessageManagerBuilder;
import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManagerBuilder;
import com.jive.sip.transport.udp.netty.NettyUdpListener;

/**
 * Keeps track of multiple {@link UdpListener} instances.
 * 
 * Because UDP doesn't provide any in-ordering guarantees, multiple threads can be used for receiving packets if needed.
 * The underlying listener may need to implement locking though, depending on what it is doing.
 * 
 * @author theo
 * 
 */

@Slf4j
public class UdpTransportManager extends AbstractService
{

  private final Map<ListenerId, NettyUdpListener> listeners = Maps.newHashMap();
  private final SipMessageManager manager = new RfcSipMessageManagerBuilder().build();
  private DispatchingEventListener<UdpTransportListener> dispatchers;

  private final EventLoopGroup group;

  @Getter
  private RfcSerializerManager serializer = new RfcSerializerManagerBuilder().build();

  @Value
  private static class Dispatcher
  {
    UdpTransportListener listener;
    Executor executor;
  }

  public UdpTransportManager(String id)
  {
    this(id, new NioEventLoopGroup(1, new ThreadFactoryBuilder().setNameFormat(String.format("%s-udp-%%s", id)).build()));
  }

  public UdpTransportManager(EventLoopGroup group)
  {
    this(null, group);
  }

  public UdpTransportManager(String id, EventLoopGroup group)
  {
    this.group = group;
    dispatchers = DispatchingEventListener.create(UdpTransportListener.class);
  }

  public void addListener(final UdpTransportListener listener, final Executor executor)
  {
    this.dispatchers.add(listener, executor);
  }

  public InetSocketAddress addListener(final ListenerId lid, final InetSocketAddress addr)
  {
    log.info("Adding listener {} to {} with handler {}", lid, addr);
    final NettyUdpListener listener = new NettyUdpListener(this, this.group, lid);
    InetSocketAddress self = listener.bind(addr);
    log.info("ListenerId({}) on {}:{}", lid.getListenerId(), self.getAddress().getHostAddress(), self.getPort());
    this.listeners.put(lid, listener);
    return self;
  }

  public EventLoopGroup getEventLoopGroup()
  {
    return this.group;
  }


  public void removeListener(final ListenerId lid)
  {
    log.info("Removing listener {}", lid);
    final NettyUdpListener listener = this.listeners.remove(lid);
    listener.close();
  }

  public SipMessageManager getSipMessageManager()
  {
    return this.manager;
  }

  public UdpTransportListener getInvoker()
  {
    return dispatchers.getInvoker();
  }


  /**
   * Send a message using the given flow.
   * 
   * @param flowId
   * @param message
   * 
   * @throws IllegalArgumentException
   *           If the ListenerId in the {@link UdpFlowId} is invalid.
   * 
   */

  public void send(final UdpFlowId flowId, final SipMessage message)
  {
    Preconditions.checkNotNull(flowId);
    Preconditions.checkNotNull(message);
    final NettyUdpListener listener = this.listeners.get(flowId.getListenerId());
    Preconditions.checkArgument(listener != null, "Invalid ListenerId");
    listener.send(flowId.getRemote(), message);
  }

  public void sendKeepalive(UdpFlowId flow)
  {
    final NettyUdpListener listener = this.listeners.get(flow.getListenerId());
    listener.sendKeepalive(flow);
  }

  @Override
  protected void doStart()
  {
    notifyStarted();
  }

  @Override
  protected void doStop()
  {

    log.info("Shutting down UdpTransportManager");

    GenericFutureListener f = new GenericFutureListener<Future<?>>()
    {
      @Override
      public void operationComplete(Future<?> future) throws Exception
      {
        notifyStopped();
      }
    };
    
    group.shutdownGracefully(100, 100, TimeUnit.MILLISECONDS).addListener(f);
    
  }

  public int getPort(UdpFlowId flowId)
  {
    final NettyUdpListener listener = this.listeners.get(flowId.getListenerId());
    Preconditions.checkArgument(listener != null, "Invalid ListenerId");
    return listener.getPort();
  }


}
