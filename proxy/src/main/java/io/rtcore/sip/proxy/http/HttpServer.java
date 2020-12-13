package io.rtcore.sip.proxy.http;

import java.security.cert.CertificateException;

import javax.net.ssl.SSLException;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractService;
import com.google.common.util.concurrent.Service;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.ApplicationProtocolNegotiationHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpServer extends AbstractService {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(HttpServer.class);

  private SslContext sslCtx;
  private SelfSignedCertificate ssl;
  private Channel ch;
  private EventLoopGroup group = new NioEventLoopGroup();
  private EventBus bus;
  private int port;

  private HttpServer(EventBus bus, int port) {
    this.port = port;
    this.bus = bus;
    try {
      this.ssl = new SelfSignedCertificate();
      this.sslCtx =
        SslContextBuilder.forServer(ssl.certificate(), ssl.privateKey())
          .sslProvider(SslProvider.JDK)
          .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
          .applicationProtocolConfig(
            new ApplicationProtocolConfig(
              Protocol.ALPN,
              SelectorFailureBehavior.NO_ADVERTISE,
              SelectedListenerFailureBehavior.ACCEPT,
              ApplicationProtocolNames.HTTP_2))
          .build();
    }
    catch (CertificateException | SSLException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  private ApplicationProtocolNegotiationHandler getServerAPNHandler() {

    return new ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_2) {

      @Override
      protected void configurePipeline(ChannelHandlerContext ctx, String protocol) throws Exception {
        if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
          ctx.pipeline()
            // .addLast(new LoggingHandler(LogLevel.INFO))
            .addLast(
              Http2FrameCodecBuilder
                .forServer()
                .autoAckPingFrame(true)
                .autoAckSettingsFrame(true)
                // .decoderEnforceMaxConsecutiveEmptyDataFrames(1)
                // .encoderEnforceMaxConcurrentStreams(true)
                .validateHeaders(true)
                .build(),
              new Http2ServerResponseHandler(bus));
          return;
        }
        throw new IllegalStateException("Protocol: " + protocol + " not supported");
      }
    };

  }

  @Override
  protected void doStart() {
    try {

      ServerBootstrap b = new ServerBootstrap();

      b.option(ChannelOption.SO_BACKLOG, 1024);

      b
        .group(group)
        .channel(NioServerSocketChannel.class)
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer<>() {

          @Override
          protected void initChannel(Channel ch) throws Exception {
            if (sslCtx != null) {
              ch.pipeline().addLast(sslCtx.newHandler(ch.alloc()), getServerAPNHandler());
            }
          }

        });

      this.ch = b.bind(this.port).sync().channel();

      ch.closeFuture()
        .addListener(new GenericFutureListener<Future<? super Void>>() {

          @Override
          public void operationComplete(Future<? super Void> future) throws Exception {
            try {
              future.get();
              notifyStopped();
            }
            catch (Throwable ex) {
              notifyFailed(ex);
            }
          }

        });

      log.info("HTTP/2 Server is listening on port {}", this.port);

      super.notifyStarted();
      
    }
    catch (InterruptedException e) {
      super.notifyFailed(e);
    }

  }

  @Override
  protected void doStop() {
    // TODO: wait for all clients to finish?
    this.ch.close();
  }

  public static Service forPort(EventBus bus, int port) {
    return new HttpServer(bus, port);
  }

}
