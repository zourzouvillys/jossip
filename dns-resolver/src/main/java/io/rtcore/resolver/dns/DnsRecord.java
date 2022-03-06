package io.rtcore.resolver.dns;

import java.time.Instant;
import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
public interface DnsRecord<T> {

  @Value.Parameter
  Instant validUntil();

  @Value.Parameter
  List<T> entries();

}
