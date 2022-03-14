package io.rtcore.sip.channels.api;

import io.rtcore.sip.channels.api.SipServerExchange.Listener;
import io.rtcore.sip.channels.internal.SipAttributes;

public class InterceptingSipServerExchangeHandler implements SipServerExchangeHandler {

  private final SipServerExchangeInterceptor interceptor;
  private final SipServerExchangeHandler handler;

  public InterceptingSipServerExchangeHandler(SipServerExchangeInterceptor interceptor, SipServerExchangeHandler handler) {
    this.interceptor = interceptor;
    this.handler = handler;
  }

  @Override
  public Listener startExchange(SipServerExchange exchange, SipAttributes attrs) {
    return this.interceptor.interceptExchange(exchange, attrs, this.handler);
  }

}
