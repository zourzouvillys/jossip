package io.rtcore.gateway.engine.http;

import java.io.UncheckedIOException;
import java.net.URI;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodySubscriber;
import java.net.http.HttpResponse.BodySubscribers;
import java.net.http.HttpResponse.ResponseInfo;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.gateway.engine.ServerTxnHandle;
import io.rtcore.gateway.engine.SipHeaderMultimap;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.NameAddrParser;
import io.rtcore.sip.message.processor.uri.SipUriExtractor;
import io.rtcore.sip.message.uri.SipUri;

public class RegisterMapper implements HttpCallMapper {

  private static final Logger log = LoggerFactory.getLogger(RegisterMapper.class);

  private static final ObjectMapper mapper =
    new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .registerModule(new GuavaModule())
      .registerModule(new Jdk8Module());

  private final SipHeaderMultimap requestHeaders;
  private final NameAddr contact;
  private final SipUri to;
  private final SipRequestFrame frame;

  public RegisterMapper(final SipRequestFrame request, final SipAttributes attrs) {

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
    return URI.create(
      String.format(
        "http://localhost:8080/edge/registrar/%s/%s/%s/%d",
        this.to.hostName().toLowerCase(),
        this.to.getUsername().orElseThrow(),
        this.contact.instanceId().map(URI::toASCIIString).orElseThrow(),
        this.contact.regId().orElseThrow()));
  }

  private String body() {

    try {

      final ObjectNode payload = JsonNodeFactory.instance.objectNode();

      payload.put("contact", this.contact.uri().toASCIIString());

      this.contact.expiresSeconds().ifPresent(expires -> payload.put("expires", expires));

      return mapper.writeValueAsString(payload);

    }
    catch (final JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }

  }

  /**
   * paese standard SIP response.
   */

  public SipResponsePayload parseResponse(final String in) {
    try {
      return mapper.readValue(in, SipResponsePayload.class);
    }
    catch (final JsonProcessingException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Override
  public String method() {
    return "PUT";
  }

  @Override
  public BodyPublisher bodyPublisher() {
    return BodyPublishers.ofString(this.body());
  }

  @Override
  public BodySubscriber<Void> bodySubscriber(final ResponseInfo resInfo, final ServerTxnHandle handle) {
    final Function<String, Void> handler = (final String in) -> {
      handle.respond(this.parseResponse(in));
      return null;
    };
    return BodySubscribers.mapping(BodySubscribers.ofString(StandardCharsets.UTF_8), handler);
  }

}
