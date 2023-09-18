package io.rtcore.gateway.udp;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultAddressedEnvelope;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.socket.DatagramChannel;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.rtcore.sip.channels.netty.internal.NettySharedLoop;
import io.rtcore.sip.channels.netty.tcp.NettyUtils;
import io.rtcore.sip.frame.SipFrame;
import io.rtcore.sip.message.message.SipMessage;

/**
 * UDP socket abstraction over Netty with a SIP codec for sending and receiving SIP frames.
 *
 * This class provides an interface for working with frames of SIP packets over UDP sockets. It does
 * not internally manage SIP transactions. If transaction handling is required, it should be
 * implemented by the consumer of this API.
 *
 * This class does not perform any manipulation of SIP headers, such as adding or removing Via
 * headers. It also does not enforce strict header validation. Header manipulation and validation
 * should be handled externally as needed.
 *
 * @see <a href="https://www.ietf.org/rfc/rfc3261.txt">RFC 3261 - SIP: Session Initiation
 *      Protocol</a>
 */

public class SipDatagramSocket {

  private final Bootstrap bootstrap;
  private final InetSocketAddress bindAddress;

  private DatagramChannel channel;
  private InetSocketAddress localSocketAddress;

  /**
   *
   */

  private SipDatagramSocket(final SipDatagramSocketConfig config) {

    final Bootstrap bootstrap = new Bootstrap();

    this.bindAddress = config.bindAddress();

    if (bootstrap.config().group() == null) {
      bootstrap.group(NettySharedLoop.allocate());
    }

    if (bootstrap.config().channelFactory() == null) {
      bootstrap.channelFactory(new ReflectiveChannelFactory<>(NioDatagramChannel.class));
    }

    this.bootstrap =
      bootstrap
        .handler(new SipDatagramChannelInitializer(this, config))
        // we never auto-close on write failure, as a single write failure to one does
        // not imply permanent failure.
        .option(ChannelOption.AUTO_CLOSE, false)
        // read automatically. might want to change this if we add more complex flow control,
        // although with shared UDP socket it's pretty much impossible to flow control properly
        // and we need to accept packets can (and are) lost.
        .option(ChannelOption.AUTO_READ, true)
        .option(ChannelOption.SO_REUSEADDR, true);
    //
  }

  /**
   *
   */

  public SipDatagramSocket bindNow() {
    this.channel = (DatagramChannel) this.bootstrap.bind(this.bindAddress).awaitUninterruptibly().channel();
    this.localSocketAddress = this.channel.localAddress();
    return this;
  }

  /**
   *
   */

  public SipDatagramSocket close() {
    try {
      this.channel.close().get();
      return this;
    }
    catch (InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * transmits a {@link SipFrame} on this channel.
   */

  public CompletableFuture<?> transmit(final InetSocketAddress recipient, final SipFrame frame) {
    return NettyUtils.toCompletableFuture(this.channel.writeAndFlush(new DefaultAddressedEnvelope<>(frame, recipient)));
  }

  /**
   * transmits a {@link SipMessage} on this channel.
   *
   * @return
   */

  public CompletableFuture<?> transmit(final InetSocketAddress recipient, final SipMessage message) {
    return NettyUtils.toCompletableFuture(this.channel.writeAndFlush(new DefaultAddressedEnvelope<>(message, recipient)));
  }

  /**
   * create new instance
   */

  public static SipDatagramSocket create(final Consumer<ImmutableSipDatagramSocketConfig.Builder> builder) {
    final ImmutableSipDatagramSocketConfig.Builder b = ImmutableSipDatagramSocketConfig.builder();
    builder.accept(b);
    return create(b.build());
  }

  /**
   * create new instance
   */

  public static SipDatagramSocket create(final ImmutableSipDatagramSocketConfig config) {
    return new SipDatagramSocket(config);
  }

  public InetSocketAddress localSocketAddress() {
    return this.localSocketAddress;
  }

  @Override
  public String toString() {
    return "SipDatagramSocket(" + this.localSocketAddress + ")";
  }

}
