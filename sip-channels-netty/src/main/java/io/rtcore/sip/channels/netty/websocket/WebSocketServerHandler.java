package io.rtcore.sip.channels.netty.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;
import io.rtcore.sip.channels.netty.tcp.ImmutableTcpConnectionConfig;

public class WebSocketServerHandler extends ChannelInitializer<NioSocketChannel> {

  private final WebSocketSipConnectionFactory connectionFactory;

  public WebSocketServerHandler(
      final SslContext sslctx,
      final ImmutableTcpConnectionConfig initialTcpConfig,
      final WebSocketSipConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Override
  protected void initChannel(final NioSocketChannel ch) throws Exception {
    final ChannelPipeline pipeline = ch.pipeline();
    pipeline.addLast("httpServerCodec", new HttpServerCodec());
    pipeline.addLast("httpHandler", new HttpServerHandler(this.connectionFactory));

  }

}
