package io.rtcore.sip.common.iana;

public interface SipMethodId {

  String token();

  default SipMethods toStandard() {
    return null;
  }

}
