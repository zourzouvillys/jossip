package io.rtcore.gateway.client;

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Verify;

import io.rtcore.gateway.api.ImmutableNICTRequest;
import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipHeaders;
import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.common.iana.SipMethods;

public final class SipGateway {

  private static final ObjectMapper mapper =
    new ObjectMapper()
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule())
      .registerModule(new GuavaModule());

  interface ClientInviteDelegate {

    void onNext(SipResponsePayload res);

    default void onComplete() {
    }

    void onError(Throwable t);

  }

  private final HttpClient httpClient;
  private final URI endpointUrl;

  public SipGateway(final URI endpoint) {
    this.endpointUrl = endpoint;
    this.httpClient =
      HttpClient.newBuilder()
        .build();
  }

  public CompletableFuture<?> INVITE(
      final URI uri,
      final Collection<SipHeaderLine> headers,
      final Optional<String> body,
      final ClientInviteDelegate delegate) {

    return this.httpClient
      .sendAsync(this.createRequest(SipMethods.INVITE, uri, headers, body), this.bodySubscriber(delegate))
      .thenApply(res -> {
        if (res.statusCode() >= 300) {
          throw new IllegalArgumentException("failed to send INVITE, HTTP status " + res.statusCode());
        }
        return res;
      })
      .thenApply(res -> res.body());

  }

  /**
   *
   * @param uri
   * @param headers
   * @return
   */

  public CompletableFuture<?> ACK(
      final URI uri,
      final Collection<SipHeaderLine> headers,
      final Optional<String> body) {

    return this.httpClient
      .sendAsync(this.createRequest(SipMethods.ACK, uri, headers, body), BodyHandlers.discarding())
      .thenApply(res -> {
        if (res.statusCode() != 202) {
          throw new IllegalArgumentException("failed to send ACK");
        }
        return res;
      });

  }

  /**
   * send a non-invite request.
   *
   *
   * @param method
   *          The method, can not be INVITE or ACK.
   * @param uri
   *          The R-URI.
   * @param headers
   *          The headers to send.
   * @param body
   *          request bosy.
   *
   * @return
   */

  public CompletableFuture<SipResponsePayload> request(
      final SipMethodId method,
      final URI uri,
      final Collection<SipHeaderLine> headers,
      final Optional<String> body) {

    // must not be used for INVITE or ACK. ACK does not return any response (only maybe error), and
    // INVITE returns multiple responses.
    Verify.verify((method != SipMethods.ACK) && (method != SipMethods.INVITE), method.token());

    return this.httpClient
      .sendAsync(this.createRequest(method, uri, headers, body), this.bodySubscriber())
      .thenApply(res -> {
        if (res.statusCode() >= 300) {
          throw new IllegalArgumentException("failed to send " + method + ": HTTP status " + res.statusCode());
        }
        return res;
      })
      .thenApply(res -> res.body());

  }

  // ----

  private HttpRequest createRequest(
      final SipMethodId method,
      final URI uri,
      final Collection<SipHeaderLine> headers,
      final Optional<String> body) {

    final ImmutableNICTRequest req =
      ImmutableNICTRequest.builder()
        .method(method)
        .uri(uri.toASCIIString())
        .headers(SipHeaders.of(headers))
        .body(body)
        .build();

    try {

      final String reqbody = mapper.writeValueAsString(req);

      return HttpRequest.newBuilder()
        .uri(this.endpointUrl)
        .header("accept", "application/json, application/x-ndjson")
        .header("content-type", "application/json")
        .PUT(BodyPublishers.ofString(reqbody))
        .build();

    }
    catch (final JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }

  }

  private BodyHandler<Void> bodySubscriber(final ClientInviteDelegate delegate) {
    return res -> {

      if (res.statusCode() > 200) {
        return BodySubscribers.replacing(null);
      }

      return BodySubscribers.fromLineSubscriber(new Subscriber<String>() {

        @Override
        public void onSubscribe(final Subscription subscription) {
          subscription.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(final String item) {
          delegate.onNext(SipGateway.this.parseResponse(item));
        }

        @Override
        public void onError(final Throwable throwable) {
          delegate.onError(throwable);
        }

        @Override
        public void onComplete() {
          delegate.onComplete();
        }

      });

    };

  }

  private BodyHandler<SipResponsePayload> bodySubscriber() {
    return res -> {
      if (res.statusCode() > 200) {
        return BodySubscribers.replacing(null);
      }
      return BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8), this::parseResponse);
    };
  }

  private SipResponsePayload parseResponse(final String t) {
    try {
      return mapper.readValue(t, SipResponsePayload.class);
    }
    catch (final JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

}
