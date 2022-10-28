package io.rtcore.resolver.dns;

import java.util.Set;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface DnsRecord<T> {

  @Value.Parameter
  int ttl();

  @Value.Parameter
  Set<T> entries();

}
