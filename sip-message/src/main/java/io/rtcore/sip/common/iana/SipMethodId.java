package io.rtcore.sip.common.iana;

public sealed interface SipMethodId permits SipMethods, UnknownSipMethod {

  String token();

  default SipMethods toStandard() {
    return null;
  }

}
