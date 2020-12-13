package io.rtcore.sip.proxy.transport.stream.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.flow.FlowControlHandler;
import io.netty.handler.flush.FlushConsolidationHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.handler.timeout.IdleStateHandler;
import io.rtcore.sip.netty.codec.SipCodec;
import io.rtcore.sip.netty.codec.SipObjectAggregator;
import io.rtcore.sip.proxy.transport.stream.ProxyProtocolHandler;
import io.rtcore.sip.proxy.transport.stream.SipStreamChannelHandler;

/**
 * service which listens for new incoming streams.
 * 
 * on a new connection, we perform a query for the policy + routing rules for this stream.
 * 
 * we optimize for a large number of mostly idle connections.
 * 
 */

public class StreamListenerService extends AbstractService {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(StreamListenerService.class);

  /**
   * 
   */

  private EventLoopGroup elg;
  private StreamListenerSpec spec;
  private EventBus eventBus;
  private Channel ch;

  StreamListenerService(EventBus eventBus, StreamListenerSpec spec) {
    this(eventBus, new NioEventLoopGroup(1), spec);
  }

  StreamListenerService(EventBus eventBus, EventLoopGroup elg, StreamListenerSpec spec) {
    this.elg = elg;
    this.eventBus = eventBus;
    this.spec = spec;
  }

  public class SipServerHandlerInit extends ChannelInitializer<SocketChannel> {

    private final SslContext sslctx;
    private final InetSocketAddress external;

    public SipServerHandlerInit(SslContext sslctx, InetSocketAddress external) {
      this.sslctx = sslctx;
      this.external = external;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

      ArrayList<ChannelHandler> handlers = new ArrayList<>();

      // if proxy protocol used:
      if (spec.proxyProtocol()) {
        handlers.add(new ProxyProtocolHandler(external));
      }

      if (spec.tls()) {
        handlers.add(sslctx.newHandler(socketChannel.alloc()));
      }

      handlers.add(new FlowControlHandler());
      handlers.add(new FlushConsolidationHandler(256, true));

      handlers.add(new SipCodec());
      handlers.add(new SipObjectAggregator(65_535));

      handlers.add(new IdleStateHandler(10, 5, 0));
      handlers.add(new LoggingHandler(LogLevel.INFO));
      handlers.add(new SipStreamChannelHandler(eventBus));

      socketChannel
        .pipeline()
        .addLast(handlers.toArray(ChannelHandler[]::new));

    }

  }

  @Override
  protected void doStart() {

    try {

      log.info("starting sip stream listener for {}", this.spec);

      ServerBootstrap b = new ServerBootstrap();

      SelfSignedCertificate cert = new SelfSignedCertificate();

      SslContext sslctx =
        SslContextBuilder.forServer(cert.certificate(), cert.privateKey())
          .build();

      this.ch =
        b.group(this.elg)
          .channel(NioServerSocketChannel.class)
          .option(ChannelOption.SO_REUSEADDR, true)
          .option(ChannelOption.AUTO_READ, true)
          // .handler(new LoggingHandler(LogLevel.INFO))
          .childOption(ChannelOption.AUTO_READ, true)
          .childHandler(new SipServerHandlerInit(sslctx, spec.externalAddress()))
          .bind(spec.bindAddress())
          .syncUninterruptibly()
          .channel();

      super.notifyStarted();

    }
    catch (IOException | CertificateException ex) {
      super.notifyFailed(ex);
    }

  }

  @Override
  protected void doStop() {

    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: AbstractService.doStop invoked.");

  }

}
