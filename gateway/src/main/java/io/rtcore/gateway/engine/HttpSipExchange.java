package io.rtcore.gateway.engine;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rtcore.gateway.engine.http.HttpCallMapper;
import io.rtcore.sip.channels.api.SipFrameUtils;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.channels.api.SipServerExchange.Listener;
import io.rtcore.sip.common.iana.SipStatusCodes;

public class HttpSipExchange implements Listener {

  private static final Logger log = LoggerFactory.getLogger(HttpSipExchange.class);

  private final SipServerExchange<SipRequestFrame, SipResponseFrame> exchange;
  private final HttpRequest request;

  private final HttpClient httpClient;

  private HttpCallMapper map;

  public HttpSipExchange(final HttpClient httpClient, final SipServerExchange<SipRequestFrame, SipResponseFrame> exchange, final HttpCallMapper map) {

    this.httpClient = httpClient;

    this.exchange = exchange;

    this.map = map;

    final URI uri = this.map.uri();

    // build the request
    this.request =
      HttpRequest.newBuilder()
        .uri(uri)
        .version(HttpClient.Version.HTTP_2)
        .timeout(Duration.of(120, ChronoUnit.SECONDS)) // longest response time
        .header("content-type", "application/json")
        .header("cache-control", "no-cache")
        .header("accept", "text/event-stream, application/json")
        .method(this.map.method(), this.map.bodyPublisher())
        .build();

    log.info("sending SIP request {} to backend {}", exchange.request().initialLine().method(), uri);

    this.httpClient
      .sendAsync(this.request, resInfo -> this.map.bodySubscriber(resInfo, exchange))
      .thenApply(res -> {

        if (res.statusCode() >= 300) {
          log.warn("invalid backend status code: {}", res.statusCode());
          throw new IllegalArgumentException("invalid backend response");
        }

        return res;

      })
      // .thenAccept(res -> {
      // exchange.onNext(this.makeResponse(res));
      // exchange.onComplete();
      // })
      .thenRun(() -> exchange.onComplete())
      .exceptionally(error -> {
        exchange.onNext(SipFrameUtils.createResponse(this.exchange.request(), SipStatusCodes.SERVER_INTERNAL_ERROR));
        exchange.onComplete();
        return null;
      });

  }

  // exchange.onNext(SipFrameUtils.createResponse(exchange.request(), SipStatusCodes.OK));
  // return null;

  @Override
  public void onCancel() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipServerExchange.Listener.onCancel invoked.");
  }

}
