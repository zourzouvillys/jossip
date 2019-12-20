package io.rtcore.sip.message.parameters.api;

import java.util.Optional;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.Token;

public interface ParameterizedObject<T> {

  Optional<Parameters> getParameters();

  <R> Optional<R> getParameter(final SipParameterDefinition<R> parameterDefinition);

  T withParameter(final Token name);

  T withParameter(final Token name, final Token value);

  T withParameter(final Token name, final QuotedString value);

  T withParameter(final Token name, final HostAndPort value);

  T withParameter(final Token name, final Long value);

  T withoutParameter(final Token name);

}
