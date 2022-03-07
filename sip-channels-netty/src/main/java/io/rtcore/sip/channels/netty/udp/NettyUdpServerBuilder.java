package io.rtcore.sip.channels.netty.udp;

import java.net.InetSocketAddress;
import java.util.concurrent.Flow.Publisher;
import java.util.function.BiFunction;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.DatagramPacket;
import io.rtcore.sip.channels.internal.ManagedSipUdpSocketBuilder;

public final class NettyUdpServerBuilder implements ManagedSipUdpSocketBuilder<NettyUdpServerBuilder> {

  private final Bootstrap bootstrap = new Bootstrap();
  private Supplier<InetSocketAddress> bindAddress = NettySupport::defaultBindAddress;

  public NettyUdpServerBuilder channelFactory(final ChannelFactory<? extends DatagramChannel> channelFactory) {
    this.bootstrap.channelFactory(channelFactory);
    return this;
  }

  public NettyUdpServerBuilder channel(final Class<? extends DatagramChannel> channelType) {
    return this.channelFactory(new ReflectiveChannelFactory<>(channelType));
  }

  public NettyUdpServerBuilder eventLoop(final EventLoopGroup group) {
    this.bootstrap.group(group);
    return this;
  }

  public <O> NettyUdpServerBuilder option(final ChannelOption<O> key, final O value) {
    this.bootstrap.option(key, value);
    return this;
  }

  public NettyUdpServerBuilder handle(final BiFunction<? super DatagramPacket, ? super DatagramChannel, ? extends Publisher<?>> handler) {
    this.bootstrap.handler(new UdpProtocolHandler(handler));
    return this;
  }

  public NettyUdpServerBuilder bindAddress(final InetSocketAddress bindAddress) {
    this.bindAddress = Suppliers.ofInstance(bindAddress);
    return this;
  }

  public NettyUdpChannel bindNow(final InetSocketAddress bindAddress) {
    return new NettyUdpChannel(this.bootstrap, Suppliers.ofInstance(bindAddress));
  }

  @Override
  public NettyUdpChannel bindNow() {
    return new NettyUdpChannel(this.bootstrap, this.bindAddress);
  }

}
