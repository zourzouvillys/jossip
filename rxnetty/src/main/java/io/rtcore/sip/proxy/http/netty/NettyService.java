package io.rtcore.sip.proxy.http.netty;

import java.security.cert.CertificateException;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.SSLException;

import com.google.common.util.concurrent.AbstractService;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.ApplicationProtocolConfig;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolNames;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import io.netty.handler.ssl.SupportedCipherSuiteFilter;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class NettyService extends AbstractService {

  private final SslContext sslctx;
  private final int port;
  private final StreamDispatcher dispatcher;

  // only when running:
  private NioEventLoopGroup group;
  private Channel serverChannel;

  NettyService(SslContext sslctx, int port, StreamDispatcher dispatcher) {
    this.sslctx = sslctx;
    this.port = port;
    this.dispatcher = dispatcher;
  }

  @Override
  protected void doStart() {
    this.group = new NioEventLoopGroup();
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.option(ChannelOption.SO_BACKLOG, 1024);
      b.group(group)
        .channel(NioServerSocketChannel.class)
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            if (sslctx != null) {
              pipeline.addLast(sslctx.newHandler(ch.alloc()), Http2Util.getServerAPNHandler(dispatcher));
            }
            else {
              ch.close();
            }
          }
        });

      this.serverChannel = b.bind(port).sync().channel();
    }
    catch (InterruptedException e) {
      if (group != null) {
        group.shutdownGracefully();
      }
      throw new RuntimeException(e);
    }
    catch (Exception ex) {
      if (group != null) {
        group.shutdownGracefully();
      }
      throw ex;
    }

  }

  @Override
  protected void doStop() {
    try {
      this.serverChannel.close().get();
    }
    catch (InterruptedException | ExecutionException e) {
      super.notifyFailed(e);
    }
    group.shutdownGracefully();
    super.notifyStopped();
  }

  /**
   * bind with self-signed certificate.
   * 
   * @param port
   * @param dispatcher
   * @return
   * @throws CertificateException
   * @throws SSLException
   */

  public static final NettyService create(int port, StreamDispatcher dispatcher) throws CertificateException, SSLException {
    SelfSignedCertificate ssc = new SelfSignedCertificate();
    SslContext sslCtx =
      SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
        .sslProvider(SslProvider.JDK)
        .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
        .applicationProtocolConfig(
          new ApplicationProtocolConfig(
            Protocol.ALPN,
            SelectorFailureBehavior.NO_ADVERTISE,
            SelectedListenerFailureBehavior.ACCEPT,
            ApplicationProtocolNames.HTTP_2))
        .build();
    return new NettyService(sslCtx, port, dispatcher);
  }

}
