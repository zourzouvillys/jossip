package io.rtcore.sip.netty.codec;


public interface SipResponse extends SipMessage {

  SipVersion protocolVersion();

  SipResponseStatus status();

}
