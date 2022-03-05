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
import io.netty.handler.ssl.SslContext;
import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.connection.SipClientExchange;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipFrame;
import io.rtcore.sip.channels.connection.SipRequestFrame;

public class SipConnectionPool implements SipConnectionProvider {

  private static final Logger logger = LoggerFactory.getLogger(SipConnectionPool.class);

  private final SipConnectionProvider provider;
  private final Map<SipRoute, ManagedSipConnection> connections = new ConcurrentHashMap<>();

  public SipConnectionPool(SipConnectionProvider provider) {
    this.provider = provider;
  }

  public static SipConnectionPool createTlsPool(EventLoopGroup elg, SslContext sslctx) {
    return new SipConnectionPool(SipTlsConnectionProvider.createProvider(elg, sslctx));
  }

  @Override
  public SipConnection requestConnection(SipRoute route) {
    return this.connections.computeIfAbsent(route, this::_requestConnection).checkout();
  }

  private ManagedSipConnection _requestConnection(SipRoute route) {
    logger.info("adding connection for route {}", route);
    return new ManagedSipConnection(route, this.provider.requestConnection(route));
  }

  private class ManagedSipConnection {

    //
    private final SipRoute route;
    private final SipConnection conn;
    private final AtomicInteger refcnt = new AtomicInteger(0);

    public ManagedSipConnection(SipRoute route, SipConnection conn) {

      this.conn = conn;
      this.route = route;

      this.conn
        .closeFuture()
        .handle((val, ex) -> {
          logger.info("removing connection for {}", route);
          connections.remove(route);
          return null;
        });

    }

    public SipConnection checkout() {
      ref();
      return new CheckedOutConnection();
    }

    void ref() {
      this.refcnt.incrementAndGet();
    }

    void unref() {
      if (refcnt.decrementAndGet() == 0) {
        // release connection to unused pool.
        System.err.println("releasing connection");
      }
    }

    private class CheckedOutConnection implements SipConnection {

      private AtomicBoolean closed = new AtomicBoolean(false);

      public CheckedOutConnection() {
      }

      @Override
      public SipClientExchange exchange(SipRequestFrame req) {
        return conn.exchange(req);
      }

      @Override
      public CompletableFuture<?> send(SipFrame frame) {
        return conn.send(frame);
      }

      @Override
      public Flowable<SipFrame> frames() {
        return conn.frames();
      }

      @Override
      public void close() {
        if (this.closed.compareAndExchange(false, true) == false) {
          unref();
        }
      }

      /**
       * called when the underlying checked out connection is closed.
       */

      @Override
      public CompletionStage<?> closeFuture() {
        return conn.closeFuture();
      }

    }

  }

}
