package io.rtcore.gateway.engine.http.client;

import java.util.concurrent.Flow.Publisher;

import io.rtcore.gateway.engine.http.server.ExternalSipServerHandler;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;

public class FakeExternalSipServer implements ExternalSipServerHandler {

  @Override
  public Publisher<ResponseEvent> handleRequest(final RequestEvent in) {
    return JdkFlowAdapter.publisherToFlowPublisher(Flux.just(ResponseEvent.statusCode(404)));
  }

  @Override
  public Publisher<ResponseEvent> handleAck(final RequestEvent in) {
    return JdkFlowAdapter.publisherToFlowPublisher(Flux.empty());
  }

}
