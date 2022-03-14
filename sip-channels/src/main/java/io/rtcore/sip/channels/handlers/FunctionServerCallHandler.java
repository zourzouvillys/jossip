package io.rtcore.sip.channels.handlers;

import java.util.function.Consumer;
import java.util.function.Function;

import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipFrameUtils;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.channels.api.SipServerExchange.Listener;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.SipStatusCodes;

public final class FunctionServerCallHandler implements SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> {

  private final Function<SipRequestFrame, SipResponseFrame> handler;
  private final Consumer<SipRequestFrame> acks;

  private FunctionServerCallHandler(final Function<SipRequestFrame, SipResponseFrame> handler, final Consumer<SipRequestFrame> acks) {
    this.handler = handler;
    this.acks = acks;
  }

  @Override
  public Listener startExchange(SipServerExchange<SipRequestFrame, SipResponseFrame> call, SipAttributes attributes) {
    if (call.request().initialLine().method().toStandard() == SipMethods.ACK) {
      this.acks.accept(call.request());
      call.onComplete();
      return null;
    }
    call.onNext(handler.apply(call.request()));
    call.onComplete();
    return null;
  }

  public static FunctionServerCallHandler create(final Function<SipRequestFrame, SipResponseFrame> requests, final Consumer<SipRequestFrame> acks) {
    return new FunctionServerCallHandler(requests, acks);
  }

  public static FunctionServerCallHandler create(final Function<SipRequestFrame, SipResponseFrame> requests) {
    return new FunctionServerCallHandler(
      requests,
      acks -> {
      });
  }

  public static FunctionServerCallHandler staticResponse(SipStatusCodes status) {
    return create(req -> SipFrameUtils.createResponse(req, status));
  }

}
