package io.rtcore.sip.channels.netty.udp;

import java.net.InetSocketAddress;
import java.util.Objects;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.DatagramPacketDecoder;
import io.netty.handler.codec.DatagramPacketEncoder;
import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.netty.codec.SipObjectDecoder;
import io.rtcore.sip.channels.netty.codec.SipObjectEncoder;
import io.rtcore.sip.channels.netty.internal.NettySharedLoop;

public class NettyUdpSocket {

  private class UdpChannelInitializer extends ChannelInitializer<DatagramChannel> {
    @Override
    public void initChannel(final DatagramChannel channel) {
      new DatagramPacketDecoder(new SipObjectDecoder());
      channel.pipeline().addLast(new DatagramPacketEncoder<>(new SipObjectEncoder()));
    }
  }

  private final DatagramChannel ch;

  NettyUdpSocket(EventLoopGroup sharedLoop, InetSocketAddress bindAddress) {

    Bootstrap bootstrap = new Bootstrap();

    Objects.requireNonNull(bindAddress, "bindAddress");

    if (bootstrap.config().group() == null) {
      bootstrap.group(NettySharedLoop.allocate());
    }

    if (bootstrap.config().channelFactory() == null) {
      bootstrap.channelFactory(new ReflectiveChannelFactory<>(NioDatagramChannel.class));
    }

    this.ch =
      (DatagramChannel) bootstrap
        .handler(new UdpChannelInitializer())
        // we never auto-close on write failure, as a single write failure to one does
        // not imply permanent failure.
        .option(ChannelOption.AUTO_CLOSE, false)
        // no automatic reading, we only read based on requests from subscribers.
        .option(ChannelOption.AUTO_READ, false)

        //
        .bind(bindAddress)
        .syncUninterruptibly()
        .channel();

  }

  /**
   * 
   */

  public final void connect(Flowable<DatagramPacket> packet, Flowable<DatagramPacket> writer) {
    // this.subscribe(readSubscriber);
    // writePublisher.subscribe(this);
  }

}
