package io.rtcore.sip.channels.internal;

public interface Attributed {

  default SipAttributes attributes() {
    return SipAttributes.of();
  }

}
