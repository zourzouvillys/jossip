package io.rtcore.sip.channels.api;

public interface SipServerExchangeHandler {

  SipServerExchange.Listener startExchange(SipServerExchange exchange);

}
