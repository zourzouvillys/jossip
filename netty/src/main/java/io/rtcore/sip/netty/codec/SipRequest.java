package io.rtcore.sip.netty.codec;


public interface SipRequest extends SipMessage {

  SipMethod method();

  SipVersion protocolVersion();

  String uri();

}
