package com.jive.sip.transport.udp.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

import java.io.StringWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import com.google.common.base.Charsets;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.jive.sip.message.api.SipMessage;
import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.transport.udp.ListenerId;
import com.jive.sip.transport.udp.UdpFlowId;
import com.jive.sip.transport.udp.UdpListener;
import com.jive.sip.transport.udp.UdpTransportManager;

/**
 * Listens on a single UDP socket, and generates events when a message is received.
 *
 * To allow scalaing of threads, we don't do anythign except read and write raw data from the socket
 * with the netty {@link NioEventLoopGroup} thread. Once the packet is received, we push it into a
 * work queue, dropping if we get above a certian size.
 *
 *
 *
 * @author theo
 *
 */

@Slf4j
public class NettyUdpListener implements UdpListener
{

  private Channel listener;

  @Getter
  private final UdpTransportManager manager;

  @Getter
  private final ListenerId lid;

  final Bootstrap b = new Bootstrap();

  private InetSocketAddress local;

  public NettyUdpListener(final UdpTransportManager manager, final EventLoopGroup group, final ListenerId lid)
  {

    this.manager = manager;
    this.lid = lid;

    this.b
        .group(group)
        .channel(NioDatagramChannel.class)
        .option(ChannelOption.SO_RCVBUF, Integer.parseInt(System.getProperty("sip.udp.recvBufferSize", "8388608")))
        .option(ChannelOption.SO_SNDBUF, Integer.parseInt(System.getProperty("sip.udp.sendBufferSize", "1048576")))
        .option(ChannelOption.AUTO_CLOSE, false)
        // << we can remove this from 5.0, when the default becomes false.
        .option(ChannelOption.SO_REUSEADDR, true)
        .option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(8192))
        .option(ChannelOption.MAX_MESSAGES_PER_READ,
            Integer.parseInt(System.getProperty("sip.udp.maxMessagesPerRead", "128")))
        .handler(new NettyUdpSipMessageHandler(manager.getSipMessageManager(), this, manager.getInvoker()));

  }

  public InetSocketAddress bind(final InetSocketAddress addr)
  {
    try
    {
      this.listener = this.b.bind(addr).sync().channel();
      this.local = (InetSocketAddress) this.listener.localAddress();
      return this.local;
    }
    catch (final Exception ex)
    {
      throw new RuntimeException(ex);
    }
  }

  public void close()
  {
    log.info("Closing listener");
    this.listener.close();
  }

  public UdpFlowId createFlowId(final InetSocketAddress remote)
  {
    return UdpFlowId.create(this.lid, HostAndPort.fromParts(remote.getAddress().getHostAddress(), remote.getPort()));
  }

  public SipMessageManager getSipMessageManager()
  {
    return this.manager.getSipMessageManager();
  }

  private static final ByteBuf KEEPALIVE = Unpooled.copiedBuffer(new byte[]
  { 13, 10, 13, 10, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });

  public void sendKeepalive(final UdpFlowId flow)
  {
    final DatagramPacket pkt =
        new DatagramPacket(KEEPALIVE.retain(), new InetSocketAddress(flow.getRemote().getHost(), flow.getRemote()
            .getPort()));
    this.listener.writeAndFlush(pkt);
  }

  @SneakyThrows
  public void send(final HostAndPort remote, final SipMessage message)
  {

    // TODO: eliminate the copy and write directly to buffer instead.

    // NOTE: this seems to result in the message being truncated to this size when transmitted. --
    // tpz
    final StringWriter w = new StringWriter(8192);

    this.manager.getSerializer().serialize(w, message);

    final ByteBuf buf = Unpooled.copiedBuffer(w.toString(), Charsets.UTF_8);

    log.trace("Sending {} bytes to {}: {}", buf.readableBytes(), remote, message);

    InetAddress remoteAddress = null;
    try
    {
      remoteAddress = InetAddresses.forString(remote.getHost());
    }
    catch (IllegalArgumentException e)
    {
      remoteAddress = InetAddress.getByName(remote.getHost());
    }
    final DatagramPacket pkt = new DatagramPacket(
        buf,
        new InetSocketAddress(remoteAddress, remote.getPortOrDefault(5060)));

    this.listener.writeAndFlush(pkt);

  }

  public int getPort()
  {
    return this.local.getPort();
  }

}
