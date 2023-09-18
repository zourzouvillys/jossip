package io.rtcore.sip.channels.netty.tcp;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.ssl.SniHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.rtcore.sip.netty.codec.SipStreamCodec;

class TlsServerHandler extends ChannelInitializer<NioSocketChannel> {

  private static final Logger logger = LoggerFactory.getLogger(TlsServerHandler.class);

  private final SslContext sslctx;
  private final TlsSipConnectionFactory factory;
  private final TcpConnectionConfig config;

  public TlsServerHandler(SslContext sslctx, TcpConnectionConfig config, TlsSipConnectionFactory factory) {
    this.sslctx = sslctx;
    this.factory = factory;
    this.config = config;
  }

  @Override
  protected void initChannel(NioSocketChannel ch) throws Exception {

    if (config.proxyProtocol()) {
      initProxyProtocol(ch);
    }
    else {
      initChannelHandlers(ch);
    }

  }

  private void initProxyProtocol(NioSocketChannel ch) {

    ChannelPipeline p = ch.pipeline();
    // if we are using proxy protocol:
    p.addLast(new HAProxyMessageDecoder(1024, true));

    // p.addLast(new LoggingHandler(LogLevel.INFO));

    p.addLast(new SimpleChannelInboundHandler<HAProxyMessage>() {

      @Override
      protected void channelRead0(ChannelHandlerContext ctx, HAProxyMessage msg) throws Exception {

        switch (msg.command()) {

          case PROXY:
            // this is the one we want.
            break;

          case LOCAL:
          default:
            // just close the connection.
            logger.debug("closing LOCAL connection");
            ctx.close();
            return;

        }

        logger.info("proxy from {}:{} -> {}:{}",
          msg.sourceAddress(),
          msg.sourcePort(),
          msg.destinationAddress(),
          msg.destinationPort());

        msg.tlvs().forEach(tlv -> {

          switch (tlv.typeByteValue()) {
            case 0x3: // CRC
            case 0x4: // NOOP
              break;
            default:
              logger.info("TLV: {} ({}):\n{}", tlv.type(), tlv.typeByteValue(), ByteBufUtil.hexDump(tlv.content()));
              break;
          }

        });

        initChannelHandlers(ctx.channel());

      }

    });

  }

  private void initChannelHandlers(Channel ch) {

    ChannelPipeline p = ch.pipeline();

    logger.info("initializing channel pipeline");

    if (this.sslctx != null) {
      
      p.addLast(new SniHandler(serverName -> {
        
        logger.info("starting TLS with {}", serverName);
        return this.sslctx;
        
      }) {

        protected SslHandler newSslHandler(SslContext context, ByteBufAllocator allocator) {

          final SslHandler handler = createHandler(ch.alloc());

          // enable endpoint identification.
          SSLEngine sslEngine = handler.engine();

          SSLParameters sslParameters = sslEngine.getSSLParameters();
          sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
          // sslParameters.setServerNames(List.copyOf(this.route.remoteServerNames()));
          sslEngine.setSSLParameters(sslParameters);

          return handler;

        }

      });
    }

    //
    // p.addLast(new IdleStateHandler(0, 5, 0));

    p.addLast(new SipStreamCodec());
    // p.addLast(new SipKeepaliveHandler());

    TlsSipConnection conn = this.factory.createConnection(ch);

    //
    p.addLast(new RxHandler(conn::onFrame));

  }

  private SslHandler createHandler(ByteBufAllocator alloc) {
    return sslctx.newHandler(alloc);
  }

}
