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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.hash.Hashing;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.UnicastProcessor;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipFrame;
import io.rtcore.sip.channels.connection.SipRequestFrame;
import io.rtcore.sip.channels.connection.SipResponseFrame;
import io.rtcore.sip.channels.connection.SipRoute;
import io.rtcore.sip.channels.connection.SipServerExchange;
import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.CSeqParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ViaParser;

public final class TlsSipConnection implements SipConnection {

  private static final Logger logger = LoggerFactory.getLogger(TlsSipConnection.class);

  static record ClientBranchId(HostPort sentBy, SipMethodId method, String branchId) {
  }

  private final long keyId = System.currentTimeMillis();
  private final AtomicLong sequences = new AtomicLong(1);

  private UnicastProcessor<SipFrame> frames = UnicastProcessor.create(true);
  private CompletableFuture<Channel> channel;

  private Map<ClientBranchId, SipStreamClientExchange> clientBranches = new ConcurrentHashMap<>();
  private SipRoute route;
  private SipServerDispatcher dispatcher;

  private TlsSipConnection(EventLoopGroup eventloopGroop, SslContext sslctx, SipRoute route, SipServerDispatcher dispatcher) {

    //
    this.route = requireNonNull(route);

    this.dispatcher = dispatcher;

    //
    ChannelFuture f =
      new Bootstrap()
        .group(eventloopGroop)
        .channel(NioSocketChannel.class)
        .handler(new TlsClientHandler(route, sslctx, this::onFrame))
        .connect(route.remoteAddress(), route.localAddress().orElseGet(() -> new InetSocketAddress(0)));

    this.channel =
      toCompletableFuture(f)
        .thenCompose(c -> toCompletableFuture(c.pipeline().get(SslHandler.class).handshakeFuture()));

    this.channel.handle((ch, err) -> {
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

  TlsSipConnection(Channel ch, SipServerDispatcher dispatcher) {
    this.dispatcher = dispatcher;
    this.channel = toCompletableFuture(ch.pipeline().get(SslHandler.class).handshakeFuture());
  }

  private class ServerCall implements SipServerExchange {

    private final Listener handler;
    private final SipRequestFrame req;
    private final IncomingSipVias vias;

    ServerCall(SipRequestFrame req) {
      this.req = req;
      this.vias = new IncomingSipVias(req.headerLines());
      // todo: refcnt.
      this.handler = dispatcher.startCall(this);
    }

    @Override
    public CompletionStage<?> sendResponse(SipResponseFrame response) {
      // todo: unref if needed
      return connection().send(this.vias.apply(response));
    }

    @Override
    public SipRequestFrame request() {
      return this.req;
    }

    @Override
    public SipConnection connection() {
      return TlsSipConnection.this;
    }

    //
    @Override
    public void close(String status) {
      // todo: unref if needed
    }

    //
    @Override
    public boolean isCancelled() {
      return false;
    }

    public String toString() {
      return String.format(
        "TlsSipConnection.ServerCall(%s %s, %s)",
        request().initialLine().method(),
        request().initialLine().uri(),
        this.vias.topLine().map(line -> line.substring("SIP/2.0/TLS ".length())).orElse(""));
    }

  }

  /**
   * apply a frame
   */

  void onFrame(SipFrame frame) {

    if (frame instanceof SipRequestFrame req) {

      //
      ServerCall call = new ServerCall(req);

    }
    else if (frame instanceof SipResponseFrame res) {

      Via topVia =
        res
          .headerLines()
          .stream()
          .filter(hdr -> hdr.knownHeaderId().orElse(null) == StandardSipHeaders.VIA)
          .map(SipHeaderLine::headerValues)
          .findFirst()
          .map(ViaParser.INSTANCE::parseFirstValue)
          .orElse(null);

      if (topVia == null) {
        logger.warn("response without valid Via header");
        return;
      }

      if (!topVia.protocol().transport().equals("TLS")) {
        logger.warn("dropping response with invalid protocol transport: {}", topVia.protocol());
        return;
      }

      String branchId = topVia.branchWithoutCookie().orElse(null);

      CSeq cseq =
        res
          .headerLines()
          .stream()
          .filter(hdr -> hdr.knownHeaderId().orElse(null) == StandardSipHeaders.CSEQ)
          .map(SipHeaderLine::headerValues)
          .findFirst()
          .map(CSeqParser.INSTANCE::parseFirstValue)
          .orElse(null);

      ClientBranchId clientKey = new ClientBranchId(topVia.sentBy(), cseq.methodId(), branchId);

      SipStreamClientExchange exchange = this.clientBranches.get(clientKey);

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
  public CompletableFuture<?> send(SipFrame frame) {
    logger.debug("sending {}", frame);
    // wait for the channel to become ready itself.
    return this.channel
      // then send.
      .thenComposeAsync(ch -> toCompletableFuture(ch.writeAndFlush(frame)))
    //
    ;
  }

  @Override
  public CompletionStage<?> closeFuture() {
    return this.channel.thenComposeAsync(ch -> toCompletableFuture(ch.closeFuture()));
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

  public SipStreamClientExchange exchange(SipRequestFrame req) {

    ClientBranchId branchId = makeKey(req);

    final LinkedList<SipHeaderLine> headers = new LinkedList<>();

    headers.add(
      StandardSipHeaders.VIA
        .ofLine(new Via(
          ViaProtocol.TLS,
          branchId.sentBy,
          DefaultParameters.of()
            .withParameter("rport")
            .withToken("branch", "z9hG4bK" + branchId.branchId()))
              .encode()));

    headers.addAll(req.headerLines());

    req = req.withHeaderLines(headers);

    SipStreamClientExchange ex = new SipStreamClientExchange(this, req, branchId);

    clientBranches.put(branchId, ex);

    return ex;

  }

  /**
   * 
   */

  private ClientBranchId makeKey(SipRequestFrame req) {
    long seqId = sequences.getAndIncrement();
    String key = Hashing.farmHashFingerprint64().newHasher().putLong(keyId).putLong(ThreadLocalRandom.current().nextLong()).putLong(seqId).hash().toString();
    String branchId = String.format("%s-%06x", key, seqId);
    return new ClientBranchId(HostPort.fromHost("invalid"), req.initialLine().method(), branchId);
  }

  /**
   * create a new connection, which will start connecting immediately.
   */

  public static TlsSipConnection create(EventLoopGroup eventloopGroop, SslContext sslctx, SipRoute route, SipServerDispatcher server) {
    return new TlsSipConnection(eventloopGroop, sslctx, route, server);
  }

  public static TlsSipConnection create(EventLoopGroup eventloopGroop, SslContext sslctx, SipRoute route) {
    return new TlsSipConnection(eventloopGroop, sslctx, route, null);
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
      this.channel.get().close();
    }
    catch (InterruptedException | ExecutionException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }
  }

  public String toString() {
    if (this.channel.isDone()) {
      try {
        return String.format("%s@%8x(%s, %s)", getClass().getSimpleName(), hashCode(), this.route, this.channel.get());
      }
      catch (InterruptedException | ExecutionException e) {
        return String.format("%s@%8x(%s, [ERROR] %s)", getClass().getSimpleName(), hashCode(), this.route, e.getMessage());
      }
    }
    return String.format("%s@%8x(%s)", getClass().getSimpleName(), hashCode(), this.route, "[connecting]");
  }

}
