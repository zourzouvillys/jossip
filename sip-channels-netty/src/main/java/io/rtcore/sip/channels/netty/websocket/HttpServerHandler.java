package io.rtcore.sip.channels.netty.websocket;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.connection.SipConnections;

class HttpServerHandler extends ChannelInboundHandlerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

  private final WebSocketSipConnectionFactory connectionFactory;

  private WebSocketServerHandshaker handshaker;

  private final SipAttributes connectionAttributes;

  HttpServerHandler(final WebSocketSipConnectionFactory connectionFactory, final SipAttributes connectionAttributes) {
    this.connectionFactory = connectionFactory;
    this.connectionAttributes = connectionAttributes;
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) {

    if (msg instanceof final HttpRequest httpRequest) {

      final String path = httpRequest.uri();
      final HttpHeaders headers = httpRequest.headers();

      logger.info("URI: {}", path);

      if (path.equals("/sipws")) {

        if ("Upgrade".equalsIgnoreCase(headers.get(HttpHeaderNames.CONNECTION))
          &&
          "WebSocket".equalsIgnoreCase(headers.get(HttpHeaderNames.UPGRADE))) {
          // Do the Handshake to upgrade connection from HTTP to WebSocket protocol
          this.handleHandshake(ctx, httpRequest);
        }

        else {

          this.sendResponse(ctx, HttpResponseStatus.BAD_REQUEST);

        }

      }
      else if (path.equals("/")) {

        this.sendResponse(ctx, HttpResponseStatus.NO_CONTENT);

      }
      else {

        this.sendResponse(ctx, HttpResponseStatus.NOT_FOUND);

      }

    }
    else if (msg instanceof final LastHttpContent last) {

      // nothing to do.

    }
    else {

      logger.warn("unknown payload: {}", msg.getClass());

    }

  }

  private void sendResponse(final ChannelHandlerContext ctx, final HttpResponseStatus status) {
    final HttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, ctx.alloc().buffer(0));
    HttpUtil.setContentLength(res, 0);
    ctx.writeAndFlush(res).addListener(ChannelFutureListener.CLOSE);
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

    req.headers().forEach(h -> logger.debug("{} = {}", h.getKey(), h.getValue()));

    final SipAttributes attributes =
      this.connectionAttributes
        .withAttribute(SipConnections.ATTR_WEBSOCKET_PATH, Optional.ofNullable(req.uri()))
        .withAttribute(SipConnections.ATTR_WEBSOCKET_USER_AGENT, Optional.ofNullable(req.headers().get(HttpHeaderNames.USER_AGENT)))
        .withAttribute(SipConnections.ATTR_WEBSOCKET_ORIGIN, Optional.ofNullable(req.headers().get(HttpHeaderNames.ORIGIN)))
        .withAttribute(SipConnections.ATTR_WEBSOCKET_HOST, Optional.ofNullable(req.headers().get(HttpHeaderNames.HOST)));

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
          this.connectionFactory.createConnection(this.handshaker, f.channel(), attributes));

    });

  }

  protected String getWebSocketURL(final HttpRequest req) {
    return "ws://" + req.headers().get("Host") + req.uri();
  }
}
