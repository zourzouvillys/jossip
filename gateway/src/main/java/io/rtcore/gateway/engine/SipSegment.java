package io.rtcore.gateway.engine;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.nio.NioEventLoopGroup;
import io.rtcore.gateway.engine.http.InviteMapper;
import io.rtcore.gateway.engine.http.RegisterMapper;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipFrameUtils;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.api.SipServerConnectionDelegate;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.channels.api.SipServerExchange.Listener;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipRoute;
import io.rtcore.sip.channels.netty.codec.SipParsingUtils;
import io.rtcore.sip.channels.netty.tcp.ImmutableTcpConnectionConfig;
import io.rtcore.sip.channels.netty.tcp.SipTlsConnectionProvider;
import io.rtcore.sip.channels.netty.tcp.SipTlsServer;
import io.rtcore.sip.channels.netty.tcp.TcpConnectionConfig;
import io.rtcore.sip.channels.netty.websocket.SipWebSocketServer;
import io.rtcore.sip.channels.netty.websocket.WebSocketSipConnection;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.message.api.CSeq;

public class SipSegment implements SipServerExchangeHandler<SipRequestFrame, SipResponseFrame>, SipServerConnectionDelegate<WebSocketSipConnection> {

  private static final Logger log = LoggerFactory.getLogger(SipSegment.class);

  private final SipTlsConnectionProvider clientProvider;
  private final SipTlsServer server;
  private final HttpClient httpClient;
  private final SipWebSocketServer webSocketServer;

  private final SipRoute route;

  /**
   *
   */

  SipSegment(final NioEventLoopGroup eventLoop, final SipRoute route) {

    this.route = route;

    final InetSocketAddress listen = new InetSocketAddress(5060);

    this.httpClient =
      HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_NONE))
        .followRedirects(Redirect.NEVER)
        .build();

    final TcpConnectionConfig tcpConfig =
      ImmutableTcpConnectionConfig
        .builder()
        .build();

    this.server = SipTlsServer.createDefault(eventLoop, null, this, listen, tcpConfig);

    this.server.startAsync().awaitRunning();

    this.clientProvider = SipTlsConnectionProvider.createProvider(eventLoop, null, this);

    this.webSocketServer =
      SipWebSocketServer.createDefault(
        eventLoop,
        null,
        this,
        new InetSocketAddress(5068),
        ImmutableTcpConnectionConfig
          .builder()
          .build(),
        this);

    this.webSocketServer.startAsync();

  }

  /**
   * called when an incoming SIP request is received, forward to a transation user.
   */

  @Override
  public Listener startExchange(final SipServerExchange<SipRequestFrame, SipResponseFrame> exchange, final SipAttributes attributes) {

    final SipRequestFrame req = exchange.request();
    final Optional<String> tag = SipParsingUtils.toTag(req.headerLines());

    final SipMethods method = req.initialLine().method().toStandard();

    log.info("request {}, tag {}", method, tag.orElse("<none>"));

    // calculate the request handler

    if (tag.isPresent()) {
      // in-dialog request.
      log.info("in-dialog request received");
      if (method != SipMethods.ACK) {
        exchange.onNext(SipFrameUtils.createResponse(req, SipStatusCodes.FORBIDDEN));
      }
      exchange.onComplete();
      return null;
    }

    switch (method) {

      case REGISTER:
        return new HttpSipExchange(this.httpClient, exchange, new RegisterMapper(req, attributes));

      case INVITE:
        return new HttpSipExchange(this.httpClient, exchange, new InviteMapper(req, attributes));

      case CANCEL:
      case OPTIONS:
      case PUBLISH:
      case REFER:
      case SUBSCRIBE:
        exchange.onNext(SipFrameUtils.createResponse(req, SipStatusCodes.METHOD_NOT_ALLOWED));
        exchange.onComplete();
        return null;

      case ACK:
        break;

      case BYE:
      case INFO:
      case MESSAGE:
      case NOTIFY:
      case PRACK:
      case UPDATE:
        break;

    }

    if (method != SipMethods.ACK) {
      exchange.onNext(SipFrameUtils.createResponse(req, SipStatusCodes.NOT_IMPLEMENTED));
    }

    exchange.onComplete();
    return null;

  }

  /**
   * called from a transaction user, send over SIP transport.
   */

  public void send(final SipRequestFrame req, final OutgoingRequestDelegate delegate) {

    log.info("transmitting request over SIP: {}", req);

    // fetch a connection.
    final SipConnection conn = this.clientProvider.requestConnection(this.route);

    if (req.initialLine().method() == SipMethods.ACK) {
      conn.send(this.fixupRequest(req, conn))
        .handle((res, err) -> {
          if (err != null) {
            delegate.onError(err);
          }
          else {
            delegate.onComplete();
          }
          return null;
        });
      return;
    }

    // perform the exchange over the connection.
    conn.exchange(this.fixupRequest(req, conn))
      .responses()
      .subscribe(
        res -> delegate.onResponse(res.response()),
        delegate::onError,
        delegate::onComplete);

  }

  private SipRequestFrame fixupRequest(final SipRequestFrame req, final SipConnection conn) {

    // the headers that each request needs to be valid: To, From,
    final SipHeaderMultimap headers = SipHeaderMultimap.from(req.headerLines());

    headers.computeIfAbsent(StandardSipHeaders.TO, _h -> req.initialLine().uri().toASCIIString());
    headers.computeIfAbsent(StandardSipHeaders.FROM, _h -> "<sip:invalid>");
    headers.computeIfAbsent(StandardSipHeaders.CALL_ID, _h -> UUID.randomUUID().toString());
    headers.computeIfAbsent(StandardSipHeaders.CSEQ, _h -> CSeq.of(req.initialLine().method()).encode());

    return req.withHeaderLines(headers.toHeaderLines());

  }

  @Override
  public void onNewConnection(final WebSocketSipConnection conn) {
    log.info("NEW connection: {}", conn);
    conn.closeFuture().thenRun(() -> this.onClosedConnection(conn));
  }

  private void onClosedConnection(final WebSocketSipConnection conn) {
    log.info("CLOSED ws connection:");
  }

}
