package io.rtcore.sip.channels.netty.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;

class HttpServerHandler extends ChannelInboundHandlerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

  private final WebSocketSipConnectionFactory connectionFactory;

  private WebSocketServerHandshaker handshaker;

  HttpServerHandler(final WebSocketSipConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) {

    if (msg instanceof final HttpRequest httpRequest) {

      final HttpHeaders headers = httpRequest.headers();

      if ("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION))
        &&
        "WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))) {
        // Do the Handshake to upgrade connection from HTTP to WebSocket protocol
        this.handleHandshake(ctx, httpRequest);

      }
    }
    else {

      System.out.println("Incoming request is unknown");
      // weird.

    }

  }

  /* Do the handshaking for WebSocket request */
  protected void handleHandshake(final ChannelHandlerContext ctx, final HttpRequest req) {

    final WebSocketServerHandshakerFactory wsFactory =
      new WebSocketServerHandshakerFactory(this.getWebSocketURL(req), "sip", true);

    this.handshaker = wsFactory.newHandshaker(req);

    if (this.handshaker == null) {
      WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
      return;
    }

    final ChannelFuture f = this.handshaker.handshake(ctx.channel(), req);

    f.addListener(ch -> {

      if (!f.isSuccess()) {
        logger.warn("failed to handshake", f.cause());
        return;
      }

      logger.info("new incoming websocket: {}", ch);

      ctx
        .pipeline()
        .replace(
          HttpServerHandler.this,
          "websocketHandler",
          this.connectionFactory.createConnection(this.handshaker, f.channel()));

    });

  }

  protected String getWebSocketURL(final HttpRequest req) {
    return "ws://" + req.headers().get("Host") + req.uri();
  }
}
