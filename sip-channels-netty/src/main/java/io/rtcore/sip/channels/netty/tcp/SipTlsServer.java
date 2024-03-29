package io.rtcore.sip.channels.netty.tcp;

import static java.util.Objects.requireNonNull;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMaxBytesRecvByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.interceptors.SipServerInterceptors;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;

/**
 * service which provides incoming connection & stream oriented (TCP, TLS) connection handling. each
 * incoming channel initially triggers an event indicating the new channel, which then requires
 * acceptance along with a policy indicating the framing/codecs, rate limiting, etc.
 */

public class SipTlsServer extends AbstractService {

  private static final Logger logger = LoggerFactory.getLogger(SipTlsServer.class);

  private final InetSocketAddress listen;
  private final EventLoopGroup acceptGroup;
  private final EventLoopGroup childGroup;

  private Channel ch;
  private final ChannelHandler childHandler;
  private final DefaultMaxBytesRecvByteBufAllocator recvalloc;
  private final WriteBufferWaterMark writeWatermark;
  private final SslContext sslctx;
  private final ImmutableTcpConnectionConfig initialTcpConfig;
  private final NettySocketServerConfig config;

  public SipTlsServer(final UnaryOperator<ImmutableNettySocketServerConfig.Builder> b) {
    this(NettySocketServerConfig.create(b));
  }

  /**
   *
   * @param listen
   * @param acceptGroup
   * @param childGroup
   * @param dispatcher
   * @param factory
   */

  public SipTlsServer(final NettySocketServerConfig config) {

    this.config = config;

    this.listen = requireNonNull(config.listenAddress());

    this.initialTcpConfig = ImmutableTcpConnectionConfig.copyOf(config.tcpConfig());

    this.acceptGroup = requireNonNull(config.acceptGroup());

    this.childGroup = requireNonNull(config.childGroup());

    // calculate
    this.recvalloc = new DefaultMaxBytesRecvByteBufAllocator(8192, 8192);

    this.writeWatermark =
      new WriteBufferWaterMark(
        Math.max(4096, this.initialTcpConfig.sendBufferSize() - 4096),
        Math.max(8192, this.initialTcpConfig.sendBufferSize() + 4096));

    this.sslctx = config.sslctx().orElse(null);

    logger.info("stream server config: {}", config);

    // the handler for initializing

    this.childHandler =
      new TlsServerHandler(
        this.sslctx,
        this.initialTcpConfig,
        ch -> new TlsSipConnection(
          ch,
          config.connectionAttributes(),
          SipServerInterceptors.interceptedHandler(config.serverHandler(), config.interceptors()))
      //
      );

  }

  /**
   *
   */

  @Override
  protected void doStart() {

    this.ch =
      new ServerBootstrap()

        .group(this.acceptGroup, this.childGroup)

        .channelFactory(new ReflectiveChannelFactory<>(NioServerSocketChannel.class))

        //
        .option(ChannelOption.AUTO_READ, true) // for now.
        .option(ChannelOption.SO_BACKLOG, 256)
        .option(ChannelOption.SO_REUSEADDR, true)

        //
        .childOption(ChannelOption.AUTO_CLOSE, true) // for now
        .childOption(ChannelOption.AUTO_READ, true) // for now
        .childOption(ChannelOption.ALLOW_HALF_CLOSURE, true)
        .childOption(ChannelOption.RCVBUF_ALLOCATOR, this.recvalloc)

        // abortive close (reset) when we close, don't keep connection history around.
        // this is acceptable because we should only close on client misbehavior or abortive
        // restart.
        .childOption(ChannelOption.SO_LINGER, 0)

        // we are not going for high throughput here, keep recvbuf small to limit the speed
        // of attacks. for normal operation this is fine as round trips for small bursts will
        // just pace naturally.
        .childOption(ChannelOption.SO_RCVBUF, this.initialTcpConfig.recvBufferSize())

        //
        .childOption(ChannelOption.SO_SNDBUF, this.initialTcpConfig.sendBufferSize())

        // enable TCP keepalives.
        .childOption(ChannelOption.SO_KEEPALIVE, true)

        // configuration of the write watermark.
        .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, this.writeWatermark)

        // and the actual handler for new incoming channels.
        .childHandler(this.childHandler)

        //
        .bind(this.listen)
        .awaitUninterruptibly()
        .channel();

    logger.info("listening on {}", this.ch.localAddress());

    super.notifyStarted();

  }

  /**
   *
   */

  @Override
  protected void doStop() {
    super.notifyStopped();
  }

  public static
      SipTlsServer
      createDefault(
          final EventLoopGroup group,
          final SslContext sslctx,
          final SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> dispatcher,
          final InetSocketAddress listen,
          final TcpConnectionConfig tcpConfig) {

    return new SipTlsServer(
      NettySocketServerConfig.create(b -> b
        .acceptGroup(group)
        .childGroup(group)
        .sslctx(Optional.ofNullable(sslctx))
        .listenAddress(listen)
        .serverHandler(dispatcher)
        .tcpConfig(tcpConfig)));

  }

  public InetSocketAddress localAddress() {
    return (InetSocketAddress) this.ch.localAddress();
  }

  public static SipTlsServer createServer(final UnaryOperator<ImmutableNettySocketServerConfig.Builder> b) {
    return new SipTlsServer(b);
  }

}
