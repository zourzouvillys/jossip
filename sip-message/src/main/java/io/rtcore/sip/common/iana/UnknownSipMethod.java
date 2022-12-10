package io.rtcore.sip.common.iana;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public sealed interface UnknownSipMethod extends SipMethodId permits ImmutableUnknownSipMethod {

  @Override
  @Value.Parameter
  String token();

  static SipMethodId of(final String methodToken) {
    return ImmutableUnknownSipMethod.of(methodToken);
  }

}
