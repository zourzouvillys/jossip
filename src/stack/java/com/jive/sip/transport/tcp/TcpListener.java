package com.jive.sip.transport.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import com.jive.sip.message.api.SipMessage;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.udp.ListenerId;

@Slf4j
class TcpListener
{

  private Channel listener;
  private final TcpTransportManager manager;

  @Getter
  private final ListenerId lid;

  private final long index = 0;

  final ServerBootstrap b = new ServerBootstrap();
  private final TcpConnectionFactory factory;

  public TcpListener(final TcpTransportManager manager, final EventLoopGroup group, final ListenerId lid, final TcpConnectionFactory factory)
  {

    this.factory = factory;
    this.manager = manager;
    this.lid = lid;

    this.b.group(group)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_KEEPALIVE, true)
        .option(ChannelOption.SO_BACKLOG, 1024)
        .option(ChannelOption.SO_REUSEADDR, true);

    this.b.childHandler(new ChannelInitializer<NioSocketChannel>()
    {

      @Override
      public void initChannel(final NioSocketChannel ch) throws Exception
      {

        ch.pipeline().addLast(
            new TcpMessageEncoder(manager.getSipMessageManager()),
            new TcpMessageDecoder(manager.getSipMessageManager()),
            new TcpConnectionHandler(manager, factory, lid)
            );

      }

    });

  }

  @SneakyThrows
  public void bind(final InetSocketAddress addr)
  {
    this.listener = this.b.bind(addr).sync().channel();
  }

  public void onMessage(final TcpFlowId flow, final SipMessage msg)
  {
    if (msg instanceof SipRequest)
    {
      this.manager.getInvoker().onSipRequestReceived(flow, (SipRequest) msg);
    }
    else
    {
      this.manager.getInvoker().onSipResponseReceived(flow, (SipResponse) msg);
    }
  }

}
