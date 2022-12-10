package io.rtcore.gateway.engine.http;

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.net.MediaType;

import io.rtcore.gateway.api.ImmutableNICTRequest;
import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.gateway.engine.SipHeaderMultimap;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipFrameUtils;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.common.SipHeaders;
import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.NameAddrParser;
import io.rtcore.sip.message.processor.uri.SipUriExtractor;
import io.rtcore.sip.message.uri.SipUri;

public class InviteMapper implements HttpCallMapper {

  private static final Logger log = LoggerFactory.getLogger(InviteMapper.class);

  private static final ObjectMapper mapper =
    new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .registerModule(new GuavaModule())
      .registerModule(new Jdk8Module());

  private final SipHeaderMultimap requestHeaders;
  private final NameAddr contact;
  private final SipUri to;
  private final SipRequestFrame frame;

  public InviteMapper(final SipRequestFrame request, final SipAttributes attrs) {

    this.frame = request;

    this.requestHeaders = SipHeaderMultimap.from(request.headerLines());

    this.contact = this.requestHeaders.singleValue(StandardSipHeaders.CONTACT).map(NameAddrParser::parse).orElseThrow();

    this.to =
      this.requestHeaders.singleValue(StandardSipHeaders.TO)
        .map(NameAddrParser::parse)
        .map(NameAddr::address)
        .map(uri -> uri.apply(SipUriExtractor.getInstance()))
        .orElseThrow();

  }

  @Override
  public URI uri() {
    return URI.create(String.format("http://localhost:8080/edge/invite"));
  }

  private String body() {
    try {
      final ImmutableNICTRequest payload =
        ImmutableNICTRequest.builder()
          .method(this.frame.initialLine().method())
          .uri(this.frame.initialLine().uri().toASCIIString())
          .headers(SipHeaders.of(this.frame.headerLines()))
          .body(this.frame.body())
          .build();
      return mapper.writeValueAsString(payload);
    }
    catch (final JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }

  }

  /**
   * paese standard SIP response.
   */

  public SipResponsePayload parseResponse(final String body) {
    try {
      return mapper.readValue(body, SipResponsePayload.class);
    }
    catch (final JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public String method() {
    return "POST";
  }

  @Override
  public BodyPublisher bodyPublisher() {
    return BodyPublishers.ofString(this.body());
  }

  @Override
  public BodySubscriber<Void> bodySubscriber(final ResponseInfo resInfo, final SipServerExchange<SipRequestFrame, SipResponseFrame> exchange) {

    final MediaType contentType =
      resInfo.headers()
        .firstValue("content-type")
        .map(MediaType::parse)
        .orElse(null);

    System.err.println(contentType);

    return BodySubscribers.fromLineSubscriber(new Subscriber<String>() {

      private final List<String> buffer = new LinkedList<>();

      @Override
      public void onSubscribe(final Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
      }

      @Override
      public void onNext(final String item) {
        if (item.length() == 0) {
          return;
        }
        exchange.onNext(InviteMapper.this.makeResponse(item));
      }

      @Override
      public void onError(final Throwable throwable) {
        throwable.printStackTrace();
      }

      @Override
      public void onComplete() {
        System.err.println(" !!!!! COMPLETE ");
      }

    });

    // final Function<String, Void> handler = (final String in) -> {
    // System.err.println(in);
    // exchange.onNext(this.makeResponse(in));
    // exchange.onComplete();
    // return null;
    // };
    //
    // return BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8), handler);

  }

  private SipResponseFrame makeResponse(final String res) {
    final SipResponsePayload body = this.parseResponse(res);
    log.info("generating response: {}, {}", res, body);
    final SipStatusCodes status = SipStatusCodes.forStatusCode(body.statusCode());
    log.info("mapped to SIP {}", status);
    return SipFrameUtils.createResponse(this.frame, status, body.headers().lines());
  }
}
