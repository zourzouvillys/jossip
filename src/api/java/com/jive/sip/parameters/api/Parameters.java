package com.jive.sip.parameters.api;

import java.util.Collection;
import java.util.Optional;

import com.google.common.net.HostAndPort;
import com.jive.sip.base.api.Token;

public interface Parameters
{
  boolean contains(final Token name);

  boolean contains(final String name);

  Collection<RawParameter> getRawParameters();

  <T> Optional<T> getParameter(final SipParameterDefinition<T> parameterDefinition);

  Parameters withParameters(final Collection<RawParameter> parameters);

  Parameters withParameter(final Token name);

  Parameters withParameter(final Token name, final Token value);

  Parameters withParameter(final Token name, final QuotedString value);

  Parameters withParameter(final Token name, final HostAndPort value);

  Parameters withoutParameter(final Token name);

  Optional<String> getParameter(final String string);

  Parameters mergeParameters(final Parameters params);

  Parameters withParameter(final RawParameter param);

  boolean compareCommonParameters(final Parameters params);

  <T> Parameters withParameter(SipParameterDefinition<T> def, String value);

  <T> Parameters withParameter(SipParameterDefinition<T> def, Token value);

  <T> Parameters withParameter(SipParameterDefinition<T> def, long value);

  <T> Parameters withParameter(SipParameterDefinition<T> def, HostAndPort value);

}
