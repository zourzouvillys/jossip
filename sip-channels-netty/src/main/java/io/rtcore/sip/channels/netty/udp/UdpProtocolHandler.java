package io.rtcore.sip.channels.netty.udp;

import java.util.concurrent.Flow.Publisher;
import java.util.function.BiFunction;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;

final class UdpProtocolHandler extends SimpleChannelInboundHandler<DatagramPacket> {

  private final BiFunction<? super DatagramPacket, ? super DatagramChannel, ? extends Publisher<?>> handler;

  UdpProtocolHandler(final BiFunction<? super DatagramPacket, ? super DatagramChannel, ? extends Publisher<?>> handler) {
    this.handler = handler;
  }

  @Override
  protected void channelRead0(final ChannelHandlerContext ctx, final DatagramPacket msg) throws Exception {
    System.err.println("Packet");
    this.handler.apply(msg, (DatagramChannel) ctx.channel());
  }

  @Override
  public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) throws Exception {
    cause.printStackTrace();
  }

}
