package io.rtcore.sip.channels.netty.tcp;

import io.rtcore.sip.channels.connection.SipServerExchange;

public interface SipServerDispatcher {

  SipServerExchange.Listener startCall(SipServerExchange call);

}
