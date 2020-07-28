package io.rtcore.sip.netty.codec;


public interface SipMessage extends SipObject {

  SipHeaders headers();

  SipVersion protocolVersion();

}
