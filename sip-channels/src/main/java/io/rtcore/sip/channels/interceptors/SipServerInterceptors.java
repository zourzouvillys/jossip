package io.rtcore.sip.channels.interceptors;

import java.util.List;

import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.api.SipServerExchangeInterceptor;

public class SipServerInterceptors {

  /**
   * create a handler which dispatches to an interceptor and then forwards to a handler.
   */

  public static <ReqT, ResT> SipServerExchangeHandler<ReqT, ResT> interceptedHandler(
      SipServerExchangeInterceptor<ReqT, ResT> interceptor,
      SipServerExchangeHandler<ReqT, ResT> handler) {

    return (exchange, attrs) -> interceptor.interceptExchange(exchange, attrs, handler);

  }

  /**
   * returns an interceptor which when called, will call each interceptor in turn.
   */

  public static <ReqT, ResT> SipServerExchangeHandler<ReqT, ResT> interceptedHandler(
      SipServerExchangeHandler<ReqT, ResT> handler,
      List<SipServerExchangeInterceptor<ReqT, ResT>> interceptors) {

    for (SipServerExchangeInterceptor<ReqT, ResT> interceptor : interceptors) {
      handler = interceptedHandler(interceptor, handler);
    }

    return handler;

  }

}
