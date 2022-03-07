package io.rtcore.sip.channels.internal;

@FunctionalInterface
public interface SipWireProducer {

  SipWirePacket next();

}
