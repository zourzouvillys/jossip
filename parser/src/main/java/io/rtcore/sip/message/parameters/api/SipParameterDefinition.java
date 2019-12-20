package io.rtcore.sip.message.parameters.api;

import java.util.Optional;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.Token;

public interface SipParameterDefinition<T> {

  // TODO: Refactor to use Parameters interface.
  Optional<T> parse(final Parameters parameters);

  Token name();

  ParameterValue<T> toParameterValue(String value);

  ParameterValue<T> toParameterValue(HostAndPort value);

  ParameterValue<T> toParameterValue(long value);

}
