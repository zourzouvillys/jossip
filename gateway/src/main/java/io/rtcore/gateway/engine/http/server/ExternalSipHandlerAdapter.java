package io.rtcore.gateway.engine.http.server;

import java.nio.charset.StandardCharsets;
import java.util.function.BiFunction;

import org.reactivestreams.Publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.rtcore.gateway.engine.http.server.ExternalSipServerHandler.RequestEvent;
import io.rtcore.gateway.engine.http.server.ExternalSipServerHandler.ResponseEvent;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;
import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

public class ExternalSipHandlerAdapter {

  private static final JsonMapper jg = new JsonMapper();

  private final ExternalSipServerHandler handler;

  public ExternalSipHandlerAdapter(final ExternalSipServerHandler handler) {
    this.handler = handler;
  }

  public BiFunction<HttpServerRequest, HttpServerResponse, Publisher<Void>> adapt() {
    return (req, res) -> res
      .status(HttpResponseStatus.OK)
      .header(HttpHeaderNames.CONTENT_TYPE, "application/json-seq")
      .sendString(req.receive().aggregate().flatMapMany(buf -> this.dispatch(req, res, this.payload(buf)).map(this::writeEvent)));
  }

  private String writeEvent(final ResponseEvent e) {
    // final ObjectNode res = JsonNodeFactory.instance.objectNode().put("seq", 0);
    try {
      return jg.writeValueAsString(e);
    }
    catch (final JsonProcessingException e1) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e1);
    }
  }

  private Flux<ResponseEvent> dispatch(final HttpServerRequest req, final HttpServerResponse res, final RequestEvent payload) {
    return JdkFlowAdapter.flowPublisherToFlux(this.handler.handleRequest(payload));
  }

  private ExternalSipServerHandler.RequestEvent payload(final ByteBuf buf) {
    return ExternalSipServerHandler.RequestEvent.fromString(buf.toString(StandardCharsets.UTF_8));
  }

}
