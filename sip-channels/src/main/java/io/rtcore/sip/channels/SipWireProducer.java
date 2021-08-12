package io.rtcore.sip.channels;

@FunctionalInterface
public interface SipWireProducer {

  SipWirePacket next();

}
