package io.rtcore.gateway.engine.http;

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.util.Map;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ValueNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.net.MediaType;

import io.rtcore.gateway.api.ImmutableNICTRequest;
import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.gateway.engine.ServerTxnHandle;
import io.rtcore.gateway.engine.SipHeaderMultimap;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.common.SipHeaders;
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

  private final URI uri;

  private final Map<String, ? extends ValueNode> properties;

  public InviteMapper(final URI uri, final SipRequestFrame request, final SipAttributes attrs, final Map<String, ? extends ValueNode> properties) {

    this.uri = uri;
    this.frame = request;
    this.properties = Map.copyOf(properties);

    this.requestHeaders = SipHeaderMultimap.from(request.headerLines());

    this.contact = this.requestHeaders.singleValue(StandardSipHeaders.CONTACT).map(NameAddrParser::parse).orElseThrow();

    this.to =
      this.requestHeaders.singleValue(StandardSipHeaders.TO)
        .map(NameAddrParser::parse)
        .map(NameAddr::address)
        .map(touri -> touri.apply(SipUriExtractor.getInstance()))
        .orElseThrow();

  }

  @Override
  public URI uri() {
    return this.uri;
  }

  private String body() {
    try {
      final ImmutableNICTRequest payload =
        ImmutableNICTRequest.builder()
          .method(this.frame.initialLine().method())
          .uri(this.frame.initialLine().uri().toASCIIString())
          .headers(SipHeaders.of(this.frame.headerLines()))
          .body(this.frame.body())
          .properties(this.properties)
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
    final String body = this.body();
    log.debug("BODY {}", body);
    return BodyPublishers.ofString(body);
  }

  @Override
  public BodySubscriber<Void> bodySubscriber(final ResponseInfo resInfo, final ServerTxnHandle handle) {

    final MediaType contentType =
      resInfo.headers()
        .firstValue("content-type")
        .map(MediaType::parse)
        .orElse(null);

    if (contentType == null) {
      throw new IllegalArgumentException("missing content-type");
    }

    log.info("content-type: {}", contentType);

    return BodySubscribers.fromLineSubscriber(new Subscriber<String>() {

      @Override
      public void onSubscribe(final Subscription subscription) {
        subscription.request(Long.MAX_VALUE);
      }

      @Override
      public void onNext(final String item) {
        if (item.length() == 0) {
          return;
        }
        handle.respond(InviteMapper.this.parseResponse(item));
      }

      @Override
      public void onError(final Throwable throwable) {
        throwable.printStackTrace();
      }

      @Override
      public void onComplete() {
        // don't complete
      }

    });

  }

}
