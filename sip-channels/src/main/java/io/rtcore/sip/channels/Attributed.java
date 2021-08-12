package io.rtcore.sip.channels;

public interface Attributed {

  default SipAttributes attributes() {
    return SipAttributes.of();
  }

}
