package io.rtcore.sip.channels.netty.tcp;

public interface SipServerDispatcher {

  SipServerExchange.Listener startCall(SipServerExchange call);

}
