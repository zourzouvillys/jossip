package io.rtcore.resolver.dns;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;
import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@Value.Style(jdkOnly = true, allowedClasspathAnnotations = { Override.class })
@JsonDeserialize(builder = ImmutableDnsResponse.Builder.class)
@JsonIgnoreProperties(value = { "Question" }, ignoreUnknown = true)
public interface DnsResponse {

  @JsonProperty("Status")
  DnsStatus status();

  /** Whether the response is truncated */
  @JsonProperty("TC")
  boolean truncated();

  /** Always true for Google Public DNS */
  @JsonProperty("RD")
  boolean rd();

  /** Always true for Google Public DNS */
  @JsonProperty("RA")
  boolean ra();

  /** if all response data was validated with DNSSEC. */
  @JsonProperty("AD")
  boolean ad();

  /** Whether the client asked to disable DNSSEC */
  @JsonProperty("CD")
  boolean cd();

  @JsonProperty("Answer")
  List<DnsAnswerRecord> answer();

  @JsonProperty("Authority")
  List<DnsAnswerRecord> authority();

  @JsonProperty("Comment")
  @Nullable
  String comment();

}
