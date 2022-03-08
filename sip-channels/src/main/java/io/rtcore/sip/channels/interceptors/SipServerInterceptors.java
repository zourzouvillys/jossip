package io.rtcore.sip.channels.interceptors;

import java.util.List;

import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.api.SipServerExchangeInterceptor;

public class SipServerInterceptors {

  public static SipServerExchangeHandler interceptedHandler(SipServerExchangeInterceptor interceptor, SipServerExchangeHandler handler) {
    return exchange -> interceptor.interceptExchange(exchange, handler);
  }

  /**
   * returns an interceptor which when called, will call each interceptor in turn.
   * 
   * @param firstInterceptor
   * @param remainingInterceptors
   * @return
   */

  public static SipServerExchangeHandler interceptedHandler(SipServerExchangeHandler handler, List<SipServerExchangeInterceptor> interceptors) {
    for (SipServerExchangeInterceptor interceptor : interceptors) {
      handler = interceptedHandler(interceptor, handler);
    }
    return handler;
  }

}
