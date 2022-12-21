package io.rtcore.sip.channels.netty.tcp;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.EventLoopGroup;
import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.channels.api.SipFrame;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipConnectionProvider;
import io.rtcore.sip.channels.connection.SipRoute;

public class SipConnectionPool implements SipConnectionProvider {

  private static final Logger logger = LoggerFactory.getLogger(SipConnectionPool.class);

  private final SipConnectionProvider provider;
  private final Map<SipRoute, ManagedSipConnection> connections = new ConcurrentHashMap<>();

  public SipConnectionPool(final SipConnectionProvider provider) {
    this.provider = provider;
  }

  public static
      SipConnectionPool createTlsPool(final EventLoopGroup elg, final TlsContextProvider sslctx, final SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> handler) {
    return new SipConnectionPool(SipTlsConnectionProvider.createProvider(elg, sslctx, handler));
  }

  public static SipConnectionPool createTcpPool(final EventLoopGroup elg, final SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> handler) {
    return new SipConnectionPool(SipTlsConnectionProvider.createProvider(elg, null, handler));
  }

  @Override
  public SipConnection requestConnection(final SipRoute route) {
    return this.connections.computeIfAbsent(route, this::_requestConnection).checkout();
  }

  private ManagedSipConnection _requestConnection(final SipRoute route) {
    logger.info("adding connection for route {}", route);
    return new ManagedSipConnection(route, this.provider.requestConnection(route));
  }

  private class ManagedSipConnection {

    //
    private final SipRoute route;
    private final SipConnection conn;
    private final AtomicInteger refcnt = new AtomicInteger(0);

    public ManagedSipConnection(final SipRoute route, final SipConnection conn) {

      this.conn = conn;
      this.route = route;

      this.conn
        .closeFuture()
        .handle((val, ex) -> {
          logger.info("removing connection for {}", route);
          SipConnectionPool.this.connections.remove(route);
          return null;
        });

    }

    public SipConnection checkout() {
      this.ref();
      return new CheckedOutConnection();
    }

    void ref() {
      this.refcnt.incrementAndGet();
    }

    void unref() {
      if (this.refcnt.decrementAndGet() == 0) {
        // release connection to unused pool.
        System.err.println("releasing connection");
      }
    }

    private class CheckedOutConnection implements SipConnection {

      private final AtomicBoolean closed = new AtomicBoolean(false);

      public CheckedOutConnection() {
      }

      @Override
      public SipClientExchange exchange(final SipRequestFrame req) {
        return ManagedSipConnection.this.conn.exchange(req);
      }

      @Override
      public CompletableFuture<?> send(final SipFrame frame) {
        return ManagedSipConnection.this.conn.send(frame);
      }

      @Override
      public Flowable<SipFrame> frames() {
        return ManagedSipConnection.this.conn.frames();
      }

      @Override
      public void close() {
        if (!this.closed.compareAndExchange(false, true)) {
          ManagedSipConnection.this.unref();
        }
      }

      /**
       * called when the underlying checked out connection is closed.
       */

      @Override
      public CompletionStage<?> closeFuture() {
        return ManagedSipConnection.this.conn.closeFuture();
      }

      @Override
      public SipAttributes attributes() {
        return ManagedSipConnection.this.conn.attributes();
      }

    }

  }

}
