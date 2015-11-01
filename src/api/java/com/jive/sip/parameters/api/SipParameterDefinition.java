package com.jive.sip.parameters.api;

import java.util.Optional;

import com.google.common.net.HostAndPort;
import com.jive.sip.base.api.Token;

public interface SipParameterDefinition<T>
{
  // TODO: Refactor to use Parameters interface.
  Optional<T> parse(final Parameters parameters);

  Token getName();

  ParameterValue<T> toParameterValue(String value);

  ParameterValue<T> toParameterValue(HostAndPort value);

  ParameterValue<T> toParameterValue(long value);

}
