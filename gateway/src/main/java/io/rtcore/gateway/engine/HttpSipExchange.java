package io.rtcore.gateway.engine;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodySubscribers;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rtcore.gateway.engine.http.HttpCallMapper;
import io.rtcore.sip.channels.api.SipServerExchange.Listener;
import io.rtcore.sip.common.iana.SipStatusCodes;

public class HttpSipExchange implements Listener {

  private static final Logger log = LoggerFactory.getLogger(HttpSipExchange.class);

  private final ServerTxnHandle handle;
  private final HttpRequest request;

  private final HttpClient httpClient;

  private final HttpCallMapper map;

  public HttpSipExchange(final HttpClient httpClient, final ServerTxnHandle handle, final HttpCallMapper map) {

    this.httpClient = httpClient;

    this.handle = handle;

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

  }

  public void send() {

    log.info("sending SIP request {} to backend {}", this.handle.request().initialLine().method(), this.map.uri());

    this.httpClient
      .sendAsync(this.request, resInfo -> {
        if (resInfo.statusCode() == 200) {
          return this.map.bodySubscriber(resInfo, this.handle);
        }
        // todo: map application/problem+json to exception?
        return BodySubscribers.discarding();
      })
      .handle((res, ex) -> {

        if (ex != null) {
          log.info("error sending request: {}", ex.getMessage(), ex);
          this.handle.respond(SipStatusCodes.SERVICE_UNAVAILABLE);
          return null;
        }

        if (res.statusCode() >= 300) {
          log.warn("invalid backend status code: {}", res.statusCode());
          this.handle.respond(SipStatusCodes.SERVICE_UNAVAILABLE);
          throw new IllegalArgumentException("invalid backend response");
        }

        if (res.statusCode() == 202) {
          // accepted, don't return anything. handle will be done from callback.
          return null;
        }

        if (res != null) {
          this.handle.close();
        }

        return res;

      })
    //
    ;

  }

  @Override
  public void onCancel() {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipServerExchange.Listener.onCancel invoked.");
  }

}
