package io.rtcore.sip.common;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface NameAddress {

  @Value.Parameter
  Optional<String> displayName();

  @Value.Parameter
  Address address();

  @Value.Parameter
  default Parameters parameters() {
    return Parameters.of();
  }

}
