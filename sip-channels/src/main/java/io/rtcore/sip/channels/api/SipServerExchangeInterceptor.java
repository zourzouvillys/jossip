package io.rtcore.sip.channels.api;

@FunctionalInterface
public interface SipServerExchangeInterceptor {

  SipServerExchange.Listener interceptExchange(SipServerExchange exchange, SipServerExchangeHandler next);

}
