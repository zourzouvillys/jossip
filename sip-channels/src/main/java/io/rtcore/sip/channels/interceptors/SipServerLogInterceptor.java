package io.rtcore.sip.channels.interceptors;

import java.util.concurrent.CompletionStage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.channels.api.SipServerExchange.Listener;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.api.SipServerExchangeInterceptor;

public final class SipServerLogInterceptor implements SipServerExchangeInterceptor<SipRequestFrame, SipResponseFrame> {

  private static final Logger LOG = LoggerFactory.getLogger(SipServerLogInterceptor.class);

  public SipServerLogInterceptor() {
  }

  @Override
  public Listener interceptExchange(SipServerExchange<SipRequestFrame, SipResponseFrame> exchange, SipAttributes attrs, SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> next) {

    SipRequestFrame req = exchange.request();

    LOG.info("SIP REQUEST:");
    LOG.info("{} {} SIP/2.0", req.initialLine().method(), req.initialLine().uri());
    req.headerLines().forEach(line -> LOG.info("{}: {}", line.headerName(), line.headerValues()));
    LOG.info("");
    req.body().ifPresent(body -> LOG.info("{}", body));

    var listener = new ForwardingSipServerExchange<>(exchange) {

      @Override
      public CompletionStage<?> onNext(SipResponseFrame res) {
        LOG.info("Sending Response:");
        LOG.info("SIP/2.0 {} {}", res.initialLine().code(), res.initialLine().reason().orElse(""));
        res.headerLines().forEach(line -> LOG.info("{}: {}", line.headerName(), line.headerValues()));
        LOG.info("");
        res.body().ifPresent(body -> LOG.info("{}", body));
        return super.onNext(res);
      }

      @Override
      public void onError(Throwable error) {
        LOG.warn("Error: {}", error.getMessage(), error);
        super.onError(error);
      }

      @Override
      public void onComplete() {
        LOG.info("COMPLETE");
        super.onComplete();
      }

    };
    return new ForwardingSipServerExchangeListener(next.startExchange(listener, attrs)) {};
  }

}
