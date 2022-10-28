package io.rtcore.sip.channels.api;

@FunctionalInterface
public interface SipServerExchangeInterceptor<ReqT, ResT> {

  SipServerExchange.Listener interceptExchange(
      SipServerExchange<ReqT, ResT> exchange,
      SipAttributes attributes,
      SipServerExchangeHandler<ReqT, ResT> next);

}
