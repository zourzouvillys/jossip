package com.jive.sip.transport.tcp;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

import lombok.extern.slf4j.Slf4j;

import com.google.common.net.HostAndPort;
import com.jive.sip.message.api.SipMessage;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.udp.ListenerId;

/**
 * The netty side of the TCP connection. Dispatches to an invoker.
 * 
 * @author theo
 * 
 */
@Slf4j
public class TcpConnectionHandler extends ChannelInboundHandlerAdapter implements TcpChannel
{

  private final TcpTransportManager manager;
  private TcpFlowId flowId;
  private Channel channel;
  private final TcpConnectionFactory factory;
  private TcpTransportListener listener;
  private final ListenerId listenerId;

  public TcpConnectionHandler(final TcpTransportManager manager, final TcpConnectionFactory factory, final ListenerId lid)
  {
    this.manager = manager;
    this.factory = factory;
    this.listenerId = lid;
  }

  /**
   * 
   */

  @Override
  public void channelActive(final ChannelHandlerContext ctx) throws Exception
  {

    final Channel ch = ctx.channel();

    final InetSocketAddress ra = (InetSocketAddress) ch.remoteAddress();

    final HostAndPort remote = HostAndPort.fromParts(ra.getAddress().getHostAddress(), ra.getPort());

    this.channel = ctx.channel();
    this.flowId = this.manager.create(this, this.listenerId, remote);

    this.listener = this.factory.create(this);

    super.channelActive(ctx);

  }

  @Override
  public TcpFlowId getFlowId()
  {
    return this.flowId;
  }

  /**
   * 
   */

  @Override
  public void channelInactive(final ChannelHandlerContext ctx) throws Exception
  {
    this.manager.close(this.flowId);
    if (this.listener != null)
    {
      this.listener.onClosed();
    }
    this.channel = null;
    super.channelInactive(ctx);
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception
  {
    log.debug("Message received: {}", msg);
    if (msg instanceof SipRequest)
    {
      this.listener.onSipRequestReceived(getFlowId(), (SipRequest) msg);
    }
    else if (msg instanceof SipResponse)
    {
      this.listener.onSipResponseReceived(getFlowId(), (SipResponse) msg);
    }
    else
    {
      log.warn("Unknown message on {}: {}", getFlowId(), msg.getClass());
    }
  }

  @Override
  public void writeAndFlush(final SipMessage msg)
  {
    if (this.channel != null)
    {
      this.channel.writeAndFlush(msg);
    }
    else
    {
      log.warn("No channel for TCP flow");
    }
  }

}
