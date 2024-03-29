package io.rtcore.sip.message.parameters.api;

import java.util.Optional;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;

/**
 * A collection of SIP parameters.
 */

public abstract class BaseParameterizedObject<T> implements ParameterizedObject<T> {

  protected Parameters parameters;

  protected BaseParameterizedObject() {
  }

  @Override
  public <R> Optional<R> getParameter(final SipParameterDefinition<R> parameterDefinition) {
    if (this.parameters != null) {
      return parameterDefinition.parse(this.parameters);
    }
    return Optional.empty();
  }

  @Override
  public T withParameter(final Token name) {
    return this.withParameters(this.parameters.withParameter(name));
  }

  @Override
  public T withParameter(final Token name, final Token value) {
    return this.withParameters(this.parameters.withParameter(name, value));
  }

  @Override
  public T withParameter(final Token name, final QuotedString value) {
    return this.withParameters(this.parameters.withParameter(name, value));
  }

  @Override
  public T withParameter(final Token name, final Long token) {
    return this.withParameters(this.parameters.withParameter(name, Token.from(token)));
  }

  @Override
  public T withParameter(final Token name, final HostAndPort value) {
    return this.withParameters(this.parameters.withParameter(name, value));
  }

  @Override
  public T withoutParameter(final Token name) {
    return this.withParameters(this.parameters.withoutParameter(name));
  }

  @Override
  public Optional<Parameters> getParameters() {
    if ((this.parameters == null) || this.parameters.getRawParameters().isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(this.parameters);
  }

  public Parameters parameters() {
    if (this.parameters == null) {
      return DefaultParameters.emptyParameters();
    }
    return this.parameters;
  }

  public T replaceParameter(final SipParameterDefinition<Token> def, final Token value) {
    final Parameters val = this.parameters.withoutParameter(def.name());
    return this.withParameters(val.withParameter(def.name(), value));
  }

  public abstract T withParameters(final Parameters parameters);

  @Override
  public boolean equals(final Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof BaseParameterizedObject)) {
      return false;
    }
    final BaseParameterizedObject<?> other = (BaseParameterizedObject<?>) o;
    if (!other.canEqual(this)) {
      return false;
    }
    final Object this$parameters = this.parameters;
    final Object other$parameters = other.parameters;
    if (this$parameters == null ? other$parameters != null
                                : !this$parameters.equals(other$parameters)) {
      return false;
    }
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof BaseParameterizedObject;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $parameters = this.parameters;
    result =
      (result * PRIME)
        + ($parameters == null ? 43
                               : $parameters.hashCode());
    return result;
  }
}
