package io.rtcore.gateway.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.UnaryOperator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Longs;

import io.rtcore.gateway.Base62;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.SipStatusCategory;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;

final class ServerTxnStore {

  private static final Logger log = LoggerFactory.getLogger(ServerTxnStore.class);

  private final Map<String, ServerTxnHandle> handles = new HashMap<>();

  private final UnaryOperator<SipResponseFrame> interceptor;

  ServerTxnStore(final UnaryOperator<SipResponseFrame> interceptor) {
    this.interceptor = interceptor;
  }

  public ServerTxnHandle lookup(final String id) {
    return this.handles.get(id);
  }

  public ServerTxnHandle register(final SipServerExchange<SipRequestFrame, SipResponseFrame> exchange, final SipAttributes attributes) {

    final String id = Base62.base62Encode(Longs.toByteArray(ThreadLocalRandom.current().nextLong()));

    final SipMethodId method = exchange.request().initialLine().method();

    final SipRequestFrame frame = exchange.request();

    final ServerTxnHandle handle = new ServerTxnHandle() {

      @Override
      public SipRequestFrame request() {
        return frame;
      }

      @Override
      public void respond(SipResponseFrame out) {

        final int statusCode = out.initialLine().code();

        out = ServerTxnStore.this.interceptor.apply(out);

        log.info("sending response", out);

        //
        exchange.onNext(out);

        if (SipStatusCategory.isFinal(statusCode) && (!SipStatusCategory.isSuccess(statusCode) || (method != SipMethods.INVITE))) {
          log.info("closing txn");
          this.close();
        }

      }

      @Override
      public String id() {
        return id;
      }

      @Override
      public void close() {
        exchange.onComplete();
        ServerTxnStore.this.handles.remove(id);
      }

      @Override
      public void close(final Throwable ex) {
        exchange.onError(ex);
      }

    };

    this.handles.put(id, handle);

    return handle;

  }

}
