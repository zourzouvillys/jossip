package io.rtcore.sip.common.iana;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface UnknownSipMethod extends SipMethodId {

  @Value.Parameter
  String token();

  static SipMethodId of(String methodToken) {
    return ImmutableUnknownSipMethod.of(methodToken);
  }

}
