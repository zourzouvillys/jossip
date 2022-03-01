package io.rtcore.sip.channels.netty.tcp;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;
import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.netty.codec.SipFrame;
import io.rtcore.sip.channels.netty.codec.SipRequestFrame;

public class SipConnectionPool implements SipConnectionProvider {

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
    return new ManagedSipConnection(this.provider.requestConnection(route));
  }

  private class ManagedSipConnection {

    private SipConnection conn;
    private AtomicInteger refcnt = new AtomicInteger(0);

    public ManagedSipConnection(SipConnection conn) {
      this.conn = conn;
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
      public SipStreamClientExchange exchange(SipRequestFrame req) {
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

    }

  }

}
