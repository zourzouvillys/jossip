package io.rtcore.sip.channels.handlers;

import java.util.concurrent.Flow.Publisher;
import java.util.function.Consumer;
import java.util.function.Function;

import org.reactivestreams.FlowAdapters;

import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.SipServerCall;
import io.rtcore.sip.channels.SipServerCallHandler;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;

public final class FunctionServerCallHandler implements SipServerCallHandler {

  private final Function<SipRequest, SipResponse> handler;
  private final Consumer<SipRequest> acks;

  private FunctionServerCallHandler(final Function<SipRequest, SipResponse> handler, final Consumer<SipRequest> acks) {
    this.handler = handler;
    this.acks = acks;
  }

  @Override
  public Publisher<SipResponse> startCall(final SipServerCall call) {
    if (call.request().method().isAck()) {
      this.acks.accept(call.request());
      return FlowAdapters.toFlowPublisher(Flowable.empty());
    }
    return FlowAdapters.toFlowPublisher(Flowable.just(this.handler.apply(call.request())));
  }

  public static FunctionServerCallHandler create(final Function<SipRequest, SipResponse> requests, final Consumer<SipRequest> acks) {
    return new FunctionServerCallHandler(requests, acks);
  }

}
