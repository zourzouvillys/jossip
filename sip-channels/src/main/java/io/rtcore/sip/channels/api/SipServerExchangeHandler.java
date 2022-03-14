package io.rtcore.sip.channels.api;

import io.rtcore.sip.channels.internal.SipAttributes;

public interface SipServerExchangeHandler<ReqT, ResT> {

  SipServerExchange.Listener startExchange(SipServerExchange<ReqT, ResT> exchange, SipAttributes attributes);

}
