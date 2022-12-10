package io.rtcore.sip.channels.netty.websocket;

import static io.rtcore.sip.channels.netty.tcp.NettyUtils.toCompletableFuture;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.ssl.SslHandler;
import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.channels.api.SipFrame;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipConnections;
import io.rtcore.sip.channels.netty.NettySipAttributes;
import io.rtcore.sip.channels.netty.codec.SipObjectEncoder;
import io.rtcore.sip.channels.netty.codec.SipParsingUtils;
import io.rtcore.sip.channels.netty.tcp.IncomingSipVias;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipInitialLine;
import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.common.iana.StandardSipTransportName;
import io.rtcore.sip.message.base.api.RawMessage;
import io.rtcore.sip.message.processor.rfc3261.SipMessageManager;

public class WebSocketSipConnection extends ChannelInboundHandlerAdapter implements SipConnection {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketSipConnection.class);

  private final CompletableFuture<Channel> _ch;
  private final SipAttributes attributes;
  private final SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> dispatcher;
  private final StandardSipTransportName transportProtocol;

  private final WebSocketServerHandshaker handshaker;

  WebSocketSipConnection(
      final WebSocketServerHandshaker handshaker,
      final Channel ch,
      final SipAttributes attributes,
      final SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> dispatcher) {

    this.handshaker = handshaker;
    this._ch = CompletableFuture.completedFuture(ch);
    this.attributes = attributes;
    this.dispatcher = dispatcher;
    this.transportProtocol = StandardSipTransportName.WS;

  }

  private CompletableFuture<Channel> channel() {
    return this._ch;
  }

  @Override
  public void channelRead(final ChannelHandlerContext ctx, final Object msg) {

    if (msg instanceof WebSocketFrame) {

      if (msg instanceof BinaryWebSocketFrame) {
        logger.warn("unsupported binary websocket frame received");
      }
      else if (msg instanceof final TextWebSocketFrame frame) {
        this.handleTextFrame(ctx, frame);
      }
      else if (msg instanceof final PingWebSocketFrame frame) {
        logger.info("PING websocket frame received, sending pong: {}", ByteBufUtil.hexDump(frame.content()));
        ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
      }
      else if (msg instanceof final PongWebSocketFrame frame) {
        logger.info("PONG websocket frame received, sending pong: {}", ByteBufUtil.hexDump(frame.content()));
      }
      else if (msg instanceof final CloseWebSocketFrame frame) {
        logger.info("CLOSE websocket frame: {} ({})", frame.statusCode(), frame.reasonText());
        this.handshaker.close(ctx.channel(), frame.retain());
        // ctx.writeAndFlush(new CloseWebSocketFrame(WebSocketCloseStatus.NORMAL_CLOSURE));
      }
      else {
        logger.warn("unsupported websocket frame: {}", msg.getClass().getSimpleName());
      }

    }

  }

  private void handleTextFrame(final ChannelHandlerContext ctx, final TextWebSocketFrame frame) {

    final String text = frame.text();

    if (text.equals("\r\n\r\n")) {
      ctx.channel().writeAndFlush("\r\n");
      return;
    }

    // now parse full SIP frame.
    final RawMessage raw = SipMessageManager.defaultManager().parseRawMessage(frame.content().nioBuffer());

    // raw.getInitialLine()
    final SipInitialLine initialLine = SipParsingUtils.parseInitialLine(raw.getInitialLine());

    final List<SipHeaderLine> headers =
      raw.getHeaders()
        .stream()
        .map(e -> SipHeaderLine.of(e.name(), e.value()))
        .collect(Collectors.toList());

    final Optional<String> body =
      Optional.ofNullable(raw.getBody())
        .filter(e -> e.length > 0)
        .map(e -> new String(e, StandardCharsets.UTF_8));

    if (initialLine instanceof final SipInitialLine.RequestLine req) {
      this.dispatchServerCall(SipRequestFrame.of(req.method(), req.uri(), headers).withBody(body));
    }
    else if (initialLine instanceof final SipInitialLine.ResponseLine res) {
      this.dispatchClientResponse(SipResponseFrame.of(SipStatusCodes.forStatusCode(res.code()), headers).withBody(body));
    }

  }

  private void dispatchServerCall(final SipRequestFrame req) {
    new ServerCall(req);
  }

  SipAttributes.Builder attributesBuilder() {

    final SipAttributes.Builder b = this.attributes.toBuilder();

    final NioSocketChannel ch = (NioSocketChannel) this.channel().getNow(null);

    if (ch != null) {

      b.set(NettySipAttributes.ATTR_CHANNEL, ch);

      if (ch.localAddress() != null) {
        b.set(SipConnections.ATTR_LOCAL_ADDR, ch.localAddress());
      }

      if (ch.remoteAddress() != null) {
        b.set(SipConnections.ATTR_REMOTE_ADDR, ch.remoteAddress());
      }

      b.set(SipConnections.ATTR_TRANSPORT, this.transportProtocol);

      final SslHandler sslhandler = ch.pipeline().get(SslHandler.class);
      if (sslhandler != null) {
        final SSLSession session = sslhandler.engine().getSession();
        if (session != null) {
          b.set(SipConnections.ATTR_SSL_SESSION, session);
        }
      }

    }

    return b;

  }

  private class ServerCall implements SipServerExchange<SipRequestFrame, SipResponseFrame> {

    private final Listener handler;
    private final SipRequestFrame req;
    private final IncomingSipVias vias;
    private final SipAttributes attributes;

    ServerCall(final SipRequestFrame req) {
      this.req = req;
      this.vias = new IncomingSipVias(req.headerLines());
      this.attributes = WebSocketSipConnection.this.attributesBuilder().build();
      // todo: refcnt?
      this.handler = WebSocketSipConnection.this.dispatcher.startExchange(this, this.attributes);
    }

    @Override
    public SipAttributes attributes() {
      // .set(SipTransport.ATTR_SENT_BY, "")
      // .set(SipTransport.ATTR_BRANCH_ID, "")
      return this.attributes;
    }

    @Override
    public CompletionStage<?> onNext(final SipResponseFrame response) {
      // todo: unref if needed
      return this.connection().send(this.vias.apply(response));
    }

    @Override
    public SipRequestFrame request() {
      return this.req;
    }

    public WebSocketSipConnection connection() {
      return WebSocketSipConnection.this;
    }

    //
    @Override
    public void onError(final Throwable err) {
      // todo: unref if needed
    }

    //
    @Override
    public void onComplete() {
      // todo: unref if needed
    }

    //
    @Override
    public boolean isCancelled() {
      return false;
    }

    @Override
    public String toString() {
      return String.format(
        "WebSocketSipConnection.ServerCall(%s %s, %s)",
        this.request().initialLine().method(),
        this.request().initialLine().uri(),
        this.vias.topLine().map(line -> line.substring(line.indexOf(' '))).orElse(""));
    }

  }

  private void dispatchClientResponse(final SipResponseFrame withBody) {
    throw new UnsupportedOperationException("Unimplemented Method: WebSocketSipConnection.dispatchClientResponse invoked.");
  }

  /////

  @Override
  public CompletionStage<?> closeFuture() {
    return this.channel().thenCompose(c -> toCompletableFuture(c.closeFuture()));
  }

  @Override
  public SipClientExchange exchange(final SipRequestFrame req) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipConnection.exchange invoked.");
  }

  @Override
  public CompletableFuture<?> send(final SipFrame frame) {
    logger.debug("sending over transport: {}", frame.initialLine());
    // wait for the channel to become ready itself.
    return this.channel()
      // then send.
      .thenComposeAsync(ch -> {
        final ByteBuf buf = SipObjectEncoder.writeFrame(frame, ch.alloc().buffer());
        return toCompletableFuture(ch.writeAndFlush(new TextWebSocketFrame(buf)));
      })
    //
    ;
  }

  @Override
  public Flowable<SipFrame> frames() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipConnection.frames invoked.");
  }

  @Override
  public void close() {
    this.channel().thenApply(Channel::close);
  }

}
