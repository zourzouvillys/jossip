package io.rtcore.sip.channels.netty.tcp;

import static java.util.Objects.requireNonNull;

import java.net.InetSocketAddress;

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
  private ChannelHandler childHandler;

  private final DefaultMaxBytesRecvByteBufAllocator recvalloc;

  private final WriteBufferWaterMark writeWatermark;

  private final ImmutableTcpConnectionConfig initialConnectionConfig;

  private final SslContext sslctx;

  public SipTlsServer(InetSocketAddress listen, EventLoopGroup group, SslContext sslctx, SipServerDispatcher dispatcher) {
    this(listen, group, group, sslctx, dispatcher);
  }

  /**
   * 
   * @param listen
   * @param acceptGroup
   * @param childGroup
   * @param dispatcher
   * @param factory
   */

  public SipTlsServer(InetSocketAddress listen, EventLoopGroup acceptGroup, EventLoopGroup childGroup, SslContext sslctx, SipServerDispatcher dispatcher) {

    this.listen = requireNonNull(listen);

    this.acceptGroup = requireNonNull(acceptGroup);

    this.childGroup = requireNonNull(childGroup);

    this.initialConnectionConfig = ImmutableTcpConnectionConfig.builder().build();

    // calculate
    this.recvalloc = new DefaultMaxBytesRecvByteBufAllocator(8192, 8192);

    this.writeWatermark =
      new WriteBufferWaterMark(
        Math.max(4096, initialConnectionConfig.sendBufferSize() - 4096),
        Math.max(8192, initialConnectionConfig.sendBufferSize() + 4096));

    this.sslctx = sslctx;

    // the handler for initializing
    this.childHandler = new TlsServerHandler(sslctx, ch -> new TlsSipConnection(ch, dispatcher));

  }

  /**
   * 
   */

  @Override
  protected void doStart() {

    this.ch =
      new ServerBootstrap()

        .group(acceptGroup, childGroup)

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
        .childOption(ChannelOption.SO_RCVBUF, this.initialConnectionConfig.recvBufferSize())

        //
        .childOption(ChannelOption.SO_SNDBUF, this.initialConnectionConfig.sendBufferSize())

        // enable TCP keepalives.
        .childOption(ChannelOption.SO_KEEPALIVE, true)

        // configuration of the write watermark.
        .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, this.writeWatermark)

        // and the actual handler for new incoming channels.
        .childHandler(childHandler)

        //
        .bind(listen)
        .awaitUninterruptibly()
        .channel();

    logger.info("listening on {}", ch.localAddress());

    super.notifyStarted();

  }

  /**
   * 
   */

  @Override
  protected void doStop() {
    super.notifyStopped();
  }

  public static SipTlsServer createDefault(EventLoopGroup group, SslContext sslctx, SipServerDispatcher dispatcher, InetSocketAddress listen) {
    return new SipTlsServer(listen, group, sslctx, dispatcher);
  }

  public InetSocketAddress localAddress() {
    return (InetSocketAddress) this.ch.localAddress();
  }

}
