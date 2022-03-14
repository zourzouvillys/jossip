package io.rtcore.sip.channels.api;

import io.rtcore.sip.channels.api.SipServerExchange.Listener;

public class InterceptingSipServerExchangeHandler<ReqT, ResT> implements SipServerExchangeHandler<ReqT, ResT> {

  private final SipServerExchangeInterceptor<ReqT, ResT> interceptor;
  private final SipServerExchangeHandler<ReqT, ResT> handler;

  public InterceptingSipServerExchangeHandler(SipServerExchangeInterceptor<ReqT, ResT> interceptor, SipServerExchangeHandler<ReqT, ResT> handler) {
    this.interceptor = interceptor;
    this.handler = handler;
  }

  @Override
  public Listener startExchange(SipServerExchange<ReqT, ResT> exchange, SipAttributes attrs) {
    return this.interceptor.interceptExchange(exchange, attrs, this.handler);
  }

}
