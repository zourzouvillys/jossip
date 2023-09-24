package io.rtcore.gateway.engine.http.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.net.MediaType;

import io.rtcore.gateway.engine.ServerTxnHandle;

public class HttpClientHandler {

  private static final Logger LOG = LoggerFactory.getLogger(HttpClientHandler.class);

  private final HttpClient httpClient;

  HttpClientHandler(final HttpClient httpClient) {
    this.httpClient = httpClient;
    // .proxy(ProxySelector.of(new InetSocketAddress("xyz", 8080)))
  }

  /**
   * perform a HTTP request and map the responses to a stream of events to apply to the client
   * transaction.
   *
   * @param uri2
   * @return
   *
   * @return
   */

  CompletableFuture<HttpResponse<Void>> publish(final URI uri, final String body) {

    final String method = "POST";
    final BodyPublisher bodyPublisher = BodyPublishers.ofString(body, StandardCharsets.UTF_8);

    // build the request
    final HttpRequest request =
      HttpRequest.newBuilder()
        .uri(uri)
        .version(HttpClient.Version.HTTP_2)
        // TODO: is the timeout the TTFB, or completion?
        .timeout(Duration.of(120, ChronoUnit.SECONDS))
        .header("content-type", "application/json")
        .header("accept", "application/json-seq, application/ndjson, application/jsonl, application/json")
        .method(method, bodyPublisher)
        .build();

    // now we send the request

    // ;

    return this.httpClient.sendAsync(request, this.bodyHandler());

  }

  private BodyHandler<Void> bodyHandler() {
    return res -> this.bodySubscriber(res, null);
  }

  public BodySubscriber<Void> bodySubscriber(final ResponseInfo resInfo, final ServerTxnHandle handle) {

    final MediaType contentType =
      resInfo.headers()
        .firstValue("content-type")
        .map(MediaType::parse)
        .orElse(null);

    // if (contentType == null) {
    // throw new IllegalArgumentException("missing content-type");
    // }

    LOG.info("content-type: {}", contentType);

    return BodySubscribers.fromLineSubscriber(new Subscriber<String>() {

      @Override
      public void onSubscribe(final Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
      }

      @Override
      public void onNext(final String item) {
        System.err.println("ITEM: [" + item + "]");
      }

      @Override
      public void onError(final Throwable throwable) {
        throwable.printStackTrace();
      }

      @Override
      public void onComplete() {
        // don't complete
        System.err.println("complete");
      }

    });

  }

}
