package io.rtcore.resolver.dns;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
@JsonDeserialize(builder = ImmutableDnsAnswerRecord.Builder.class)
public interface DnsAnswerRecord {

  @JsonProperty
  String name();

  @JsonProperty
  DnsRecordType type();

  @JsonProperty("TTL")
  int ttl();

  @JsonProperty
  String data();

}
