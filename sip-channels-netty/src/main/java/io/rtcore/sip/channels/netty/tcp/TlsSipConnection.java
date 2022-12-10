package io.rtcore.sip.channels.netty.tcp;

import static io.rtcore.sip.channels.netty.tcp.NettyUtils.toCompletableFuture;
import static java.util.Objects.requireNonNull;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Verify;
import com.google.common.hash.Hashing;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslHandler;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.UnicastProcessor;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipFrame;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipConnections;
import io.rtcore.sip.channels.connection.SipRoute;
import io.rtcore.sip.channels.netty.ClientBranchId;
import io.rtcore.sip.channels.netty.NettySipAttributes;
import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.common.iana.StandardSipTransportName;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.CSeqParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ViaParser;

public final class TlsSipConnection implements SipConnection {

  private static final Logger logger = LoggerFactory.getLogger(TlsSipConnection.class);

  private final long keyId = System.currentTimeMillis();
  private final AtomicLong sequences = new AtomicLong(1);

  private final UnicastProcessor<SipFrame> frames = UnicastProcessor.create(true);
  private CompletableFuture<Channel> _channel;

  private final SipAttributes attributes;

  private final Map<ClientBranchId, SipStreamClientExchange> clientBranches = new ConcurrentHashMap<>();
  private SipRoute route;
  private SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> dispatcher;
  private CompletableFuture<Channel> _ch;

  private final StandardSipTransportName transportProtocol;

  private TlsSipConnection(
      final EventLoopGroup eventloopGroop,
      final TlsContextProvider sslctx,
      final SipRoute route,
      final SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> dispatcher) {

    this.transportProtocol =
      sslctx == null ? StandardSipTransportName.TCP
                     : StandardSipTransportName.TLS;

    this.attributes = SipAttributes.of();

    //
    this.route = requireNonNull(route);

    this.dispatcher = dispatcher;

    logger.info("new outgoing connection");

    //
    final ChannelFuture f =
      new Bootstrap()
        .group(eventloopGroop)
        .channel(NioSocketChannel.class)
        .handler(new TlsClientHandler(route, sslctx, this::onFrame))
        .connect(route.remoteAddress(), route.localAddress().orElseGet(() -> new InetSocketAddress(0)));

    // this.channel =
    // toCompletableFuture(f)
    // .thenCompose(c -> toCompletableFuture(c.pipeline().get(SslHandler.class).handshakeFuture()));

    this._ch = toCompletableFuture(f);

    this.channel().handle((ch, err) -> {
      if (err != null) {
        logger.info("error opening channel: {}", err);
      }
      else {
        logger.info("connected to {}", ch);
        this.closeFuture().handle((__, closeError) -> {
          logger.info("connection {} closed, error: {}", ch, closeError);
          return null;
        });
      }
      return null;
    });

  }

  TlsSipConnection(
      final Channel ch,
      final SipAttributes connectionAttributes,
      final SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> dispatcher) {

    logger.info("new incoming connection: {}, {}", ch, connectionAttributes);

    this.attributes = connectionAttributes;
    this.dispatcher = dispatcher;

    this.transportProtocol =
      ch.pipeline().get(SslHandler.class) == null ? StandardSipTransportName.TCP
                                                  : StandardSipTransportName.TLS;

    this._ch = CompletableFuture.completedFuture(ch);

  }

  private CompletableFuture<Channel> channel() {
    return this._ch.thenCompose(c -> {

      final SslHandler handler = c.pipeline().get(SslHandler.class);

      if (handler == null) {
        return CompletableFuture.completedStage(c);
      }

      // wait for the handshake
      return toCompletableFuture(handler.handshakeFuture());

    });
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
      this.attributes = TlsSipConnection.this.attributesBuilder().build();
      // todo: refcnt?
      this.handler = TlsSipConnection.this.dispatcher.startExchange(this, this.attributes);
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

    public TlsSipConnection connection() {
      return TlsSipConnection.this;
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
        "TlsSipConnection.ServerCall(%s %s, %s)",
        this.request().initialLine().method(),
        this.request().initialLine().uri(),
        this.vias.topLine().map(line -> line.substring("SIP/2.0/TLS ".length())).orElse(""));
    }

  }

  /**
   * apply a frame
   */

  void onFrame(final SipFrame frame) {

    if (frame instanceof final SipRequestFrame req) {

      new ServerCall(req);

    }
    else if (frame instanceof final SipResponseFrame res) {

      final Via topVia =
        res
          .headerLines()
          .stream()
          .filter(hdr -> hdr.knownHeaderId().orElse(null) == StandardSipHeaders.VIA)
          .map(SipHeaderLine::headerValues)
          .findFirst()
          .map(ViaParser.INSTANCE::parseFirstValue)
          .orElse(null);

      if (topVia == null) {
        logger.warn("response without valid Via header: {}", res);
        return;
      }

      if (!topVia.protocol().transport().equals(this.transportProtocol.id())) {
        logger.warn("dropping response with invalid protocol transport: {}, expected {}", topVia.protocol(), this.transportProtocol);
        return;
      }

      final String branchId = topVia.branchWithoutCookie().orElse(null);

      final CSeq cseq =
        res
          .headerLines()
          .stream()
          .filter(hdr -> hdr.knownHeaderId().orElse(null) == StandardSipHeaders.CSEQ)
          .map(SipHeaderLine::headerValues)
          .findFirst()
          .map(CSeqParser.INSTANCE::parseFirstValue)
          .orElse(null);

      if (cseq == null) {
        logger.warn("response without valid CSeq header: {}", res);
        return;
      }

      final ClientBranchId clientKey = new ClientBranchId(topVia.sentBy(), cseq.methodId(), branchId);

      final SipStreamClientExchange exchange = this.clientBranches.get(clientKey);

      if (exchange == null) {
        logger.warn("response for unknown sip exchange: {}", clientKey);
        return;
      }

      exchange.onResponseFrame(this, res);

    }
    else {

      logger.warn("unknown SipFrame", frame);

    }

  }

  /**
   * send a frame once
   */

  @Override
  public CompletableFuture<?> send(final SipFrame frame) {
    logger.debug("sending {}", frame);
    // wait for the channel to become ready itself.
    return this.channel()
      // then send.
      .thenComposeAsync(ch -> toCompletableFuture(ch.writeAndFlush(frame)))
    //
    ;
  }

  @Override
  public CompletionStage<?> closeFuture() {
    return this.channel().thenComposeAsync(ch -> toCompletableFuture(ch.closeFuture()));
  }

  /**
   * perform a sip exchange over this connection.
   *
   * the request must be fully formed, including the Via header and branch identifier.
   *
   * a response may be received on a different connection (e.g, if no rport), so consumers must take
   * care to assign the context of the connection transaction if it wishes for responses received on
   * other connections to be used for responses to this request.
   *
   */

  @Override
  public SipStreamClientExchange exchange(SipRequestFrame req) {

    Verify.verify(req.initialLine().method() != SipMethods.ACK);

    final ClientBranchId branchId = this.makeKey(req);

    final LinkedList<SipHeaderLine> headers = new LinkedList<>();

    headers.add(
      StandardSipHeaders.VIA
        .ofLine(new Via(
          ViaProtocol.forString(this.transportProtocol.id()),
          branchId.sentBy(),
          DefaultParameters.of()
            .withParameter("rport")
            .withToken("branch", "z9hG4bK" + branchId.branchId()))
          .encode()));

    headers.addAll(req.headerLines());

    req = req.withHeaderLines(headers);

    final SipStreamClientExchange ex = new SipStreamClientExchange(this, req, branchId);

    this.clientBranches.put(branchId, ex);

    return ex;

  }

  /**
   *
   */

  private ClientBranchId makeKey(final SipRequestFrame req) {
    final long seqId = this.sequences.getAndIncrement();
    final String key =
      Hashing.farmHashFingerprint64().newHasher().putLong(this.keyId).putLong(ThreadLocalRandom.current().nextLong()).putLong(seqId).hash().toString();
    final String branchId = String.format("%s-%06x", key, seqId);
    return new ClientBranchId(HostPort.fromHost("invalid"), req.initialLine().method(), branchId);
  }

  /**
   * create a new connection, which will start connecting immediately.
   */

  public static
      TlsSipConnection
      create(
          final EventLoopGroup eventloopGroop,
          final TlsContextProvider sslctx,
          final SipRoute route,
          final SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> server) {

    return new TlsSipConnection(eventloopGroop, sslctx, route, server);

  }

  /**
   *
   */

  @Override
  public Flowable<SipFrame> frames() {
    return this.frames;
  }

  /**
   *
   */

  @Override
  public void close() {
    try {
      this.channel().get().close();
    }
    catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {
    if (this.channel().isDone()) {
      try {
        return String.format("%s@%8x(%s, %s)", this.getClass().getSimpleName(), this.hashCode(), this.route, this.channel().get());
      }
      catch (InterruptedException | ExecutionException e) {
        return String.format("%s@%8x(%s, [ERROR] %s)", this.getClass().getSimpleName(), this.hashCode(), this.route, e.getMessage());
      }
    }
    return String.format("%s@%8x(%s)", this.getClass().getSimpleName(), this.hashCode(), this.route, "[connecting]");
  }

}
