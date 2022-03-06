package io.rtcore.resolver.dns;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface DnsRecord<T> {

  @Value.Parameter
  int ttl();

  @Value.Parameter
  List<T> entries();

}
