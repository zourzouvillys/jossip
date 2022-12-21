package io.rtcore.sip.channels.netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.netty.tcp.ImmutableTcpConnectionConfig;

public class WebSocketServerHandler extends ChannelInitializer<NioSocketChannel> {

  private final WebSocketSipConnectionFactory connectionFactory;
  private final SipAttributes connectionAttributes;

  public WebSocketServerHandler(
      final SslContext sslctx,
      final ImmutableTcpConnectionConfig initialTcpConfig,
      final SipAttributes connectionAttributes,
      final WebSocketSipConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
    this.connectionAttributes = connectionAttributes;
  }

  @Override
  protected void initChannel(final NioSocketChannel ch) throws Exception {
    final ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast("httpServerCodec", new HttpServerCodec());
    pipeline.addLast("httpAggregator", new HttpObjectAggregator(1048576));
    pipeline.addLast("httpHandler", new HttpServerHandler(this.connectionFactory, this.connectionAttributes));
  }

}
