package io.rtcore.gateway;

import java.util.concurrent.Flow.Publisher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rtcore.gateway.engine.http.server.ExternalSipServerHandler;
import reactor.adapter.JdkFlowAdapter;
import reactor.core.publisher.Flux;

public class EmptyExternalSipServer implements ExternalSipServerHandler {

  private static final Logger LOG = LoggerFactory.getLogger(EmptyExternalSipServer.class);

  @Override
  public Publisher<ResponseEvent> handleRequest(final RequestEvent in) {
    LOG.info("got request: {}", in);
    return JdkFlowAdapter.publisherToFlowPublisher(Flux.just(ResponseEvent.statusCode(404)));
  }

  @Override
  public Publisher<ResponseEvent> handleAck(final RequestEvent in) {
    LOG.info("got ACK: {}", in);
    return JdkFlowAdapter.publisherToFlowPublisher(Flux.empty());
  }

}
