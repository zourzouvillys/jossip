package io.rtcore.sip.channels.handlers;

import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipFrameUtils;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.channels.api.SipServerExchange.Listener;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.SipStatusCodes;

public class SipServerExchangeHandlers {

  public static SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> staticErorHandler(Throwable error) {
    return new SipServerExchangeHandler<SipRequestFrame, SipResponseFrame>() {
      @Override
      public Listener startExchange(SipServerExchange<SipRequestFrame, SipResponseFrame> exchange, SipAttributes attributes) {
        exchange.onError(error);
        return null;
      }
    };
  }

  public static SipServerExchangeHandler<SipRequestFrame, SipResponseFrame> staticResponseHandler(SipStatusCodes status) {
    return new SipServerExchangeHandler<SipRequestFrame, SipResponseFrame>() {
      @Override
      public Listener startExchange(SipServerExchange<SipRequestFrame, SipResponseFrame> exchange, SipAttributes attributes) {
        if (exchange.request().initialLine().method() != SipMethods.ACK) {
          exchange.onNext(SipFrameUtils.createResponse(exchange.request(), status));
        }
        exchange.onComplete();
        return null;
      }
    };
  }

}
