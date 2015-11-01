package com.jive.sip.parameters.api;

import java.util.Optional;

import com.google.common.net.HostAndPort;
import com.jive.sip.base.api.Token;

import lombok.EqualsAndHashCode;

/**
 * A collection of SIP parameters.
 * 
 * @author theo
 *
 * @param <T>
 */

@EqualsAndHashCode
public abstract class BaseParameterizedObject<T> implements ParameterizedObject<T>
{

  protected Parameters parameters;

  @Override
  public <R> Optional<R> getParameter(final SipParameterDefinition<R> parameterDefinition)
  {
    if (this.parameters != null)
    {
      return parameterDefinition.parse(this.parameters);
    }
    else
    {
      return Optional.empty();
    }
  }


  @Override
  public T withParameter(final Token name)
  {
    return this.withParameters(this.parameters.withParameter(name));
  }

  @Override
  public T withParameter(final Token name, final Token value)
  {
    return this.withParameters(this.parameters.withParameter(name, value));
  }

  @Override
  public T withParameter(final Token name, final QuotedString value)
  {
    return this.withParameters(this.parameters.withParameter(name, value));
  }

  @Override
  public T withParameter(final Token name, final Long token)
  {
    return this.withParameters(this.parameters.withParameter(name, Token.from(token)));
  }


  @Override
  public T withParameter(final Token name, final HostAndPort value)
  {
    return this.withParameters(this.parameters.withParameter(name, value));
  }

  @Override
  public T withoutParameter(final Token name)
  {
    return this.withParameters(this.parameters.withoutParameter(name));
  }

  @Override
  public Optional<Parameters> getParameters()
  {
    if ((this.parameters == null) || this.parameters.getRawParameters().isEmpty())
    {
      return Optional.empty();
    }
    else
    {
      return Optional.ofNullable(this.parameters);
    }
  }

  public T replaceParameter(SipParameterDefinition<Token> def, Token value)
  {
    Parameters val = this.parameters.withoutParameter(def.getName());
    return withParameters(val.withParameter(def.getName(), value));
  }


  abstract public T withParameters(final Parameters parameters);

}
