package io.rtcore.sip.channels.api;

public interface SipServerExchangeHandler<ReqT, ResT> {

  SipServerExchange.Listener startExchange(SipServerExchange<ReqT, ResT> exchange, SipAttributes attributes);

}
