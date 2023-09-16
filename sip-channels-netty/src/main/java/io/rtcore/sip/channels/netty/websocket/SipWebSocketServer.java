package io.rtcore.sip.channels.netty.websocket;

import static java.util.Objects.requireNonNull;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.AbstractService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultMaxBytesRecvByteBufAllocator;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.rtcore.sip.channels.api.SipServerConnectionDelegate;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.interceptors.SipServerInterceptors;
import io.rtcore.sip.channels.netty.tcp.ImmutableNettySocketServerConfig;
import io.rtcore.sip.channels.netty.tcp.ImmutableTcpConnectionConfig;
import io.rtcore.sip.channels.netty.tcp.NettySocketServerConfig;
import io.rtcore.sip.channels.netty.tcp.TcpConnectionConfig;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;

public class SipWebSocketServer extends AbstractService {

  private static final Logger logger = LoggerFactory.getLogger(SipWebSocketServer.class);

  private final SipServerConnectionDelegate<WebSocketSipConnection> delegate;
  private final NettySocketServerConfig config;
  private final InetSocketAddress listen;
  private final ImmutableTcpConnectionConfig initialTcpConfig;
  private final EventLoopGroup acceptGroup;
  private final EventLoopGroup childGroup;
  private final DefaultMaxBytesRecvByteBufAllocator recvalloc;
  private final WriteBufferWaterMark writeWatermark;
  private final SslContext sslctx;
  private final WebSocketServerHandler childHandler;
  private Channel ch;

  public SipWebSocketServer(final SipServerConnectionDelegate<WebSocketSipConnection> delegate, final NettySocketServerConfig config) {

    this.delegate = delegate;
    this.config = config;
    this.listen = requireNonNull(config.listenAddress());
    this.initialTcpConfig = ImmutableTcpConnectionConfig.copyOf(config.tcpConfig());
    this.acceptGroup = requireNonNull(config.acceptGroup());
    this.childGroup = requireNonNull(config.childGroup());
    this.recvalloc = new DefaultMaxBytesRecvByteBufAllocator(8192, 8192);

    this.writeWatermark =
      new WriteBufferWaterMark(
        Math.max(4096, this.initialTcpConfig.sendBufferSize() - 4096),
        Math.max(8192, this.initialTcpConfig.sendBufferSize() + 4096));

    this.sslctx = config.sslctx().orElse(null);

    // the handler for initializing

    this.childHandler =
      new WebSocketServerHandler(
        this.sslctx,
        this.initialTcpConfig,
        config.connectionAttributes(),
        (handshaker, ch, attributes) -> {

          final WebSocketSipConnection conn =
            new WebSocketSipConnection(
              handshaker,
              ch,
              attributes,
              SipServerInterceptors.interceptedHandler(config.serverHandler(), config.interceptors()));

          this.delegate.onNewConnection(conn);

          return conn;

        }
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
      SipWebSocketServer
      createDefault(
          final EventLoopGroup group,
          final SslContext sslctx,
          final SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> dispatcher,
          final InetSocketAddress listen,
          final TcpConnectionConfig tcpConfig,
          final SipServerConnectionDelegate<WebSocketSipConnection> delegate) {

    return new SipWebSocketServer(
      delegate,
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

  public static
      SipWebSocketServer
      createServer(
          final SipServerConnectionDelegate<WebSocketSipConnection> delegate,
          final UnaryOperator<ImmutableNettySocketServerConfig.Builder> b) {

    return new SipWebSocketServer(
      delegate,
      b.apply(ImmutableNettySocketServerConfig.builder()).build());

  }

}
