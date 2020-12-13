package io.rtcore.sip.proxy.actions;

import java.util.Map;
import java.util.Optional;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Value.Immutable
@JsonInclude(value = Include.NON_EMPTY)
@JsonDeserialize(builder = OpenStream.Builder.class)
public interface OpenStream {

  @JsonProperty
  String transport();

  @JsonProperty
  String remote();

  @JsonProperty
  Optional<String> clientToken();

  @JsonProperty
  Optional<String> serverName();

  @JsonProperty
  Map<String, String> tags();

  public static class Builder extends ImmutableOpenStream.Builder {
  }

  static Builder builder() {
    return new Builder();
  }

}
