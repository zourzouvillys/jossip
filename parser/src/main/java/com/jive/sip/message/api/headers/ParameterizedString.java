package com.jive.sip.message.api.headers;

import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;

public final class ParameterizedString extends BaseParameterizedObject<ParameterizedString> {
  private final String value;

  public ParameterizedString(String value) {
    this(value, null);
  }

  public ParameterizedString(String value, Parameters parameters) {
    this.value = value;
    this.parameters = parameters;
  }

  @Override
  public ParameterizedString withParameters(Parameters parameters) {
    return new ParameterizedString(this.value, parameters);
  }

  public String value() {
    return this.value;
  }

  @Override
  public String toString() {
    return "ParameterizedString(value=" + this.value() + ")";
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof ParameterizedString))
      return false;
    final ParameterizedString other = (ParameterizedString) o;
    if (!other.canEqual((Object) this))
      return false;
    if (!super.equals(o))
      return false;
    final Object this$value = this.value();
    final Object other$value = other.value();
    if (this$value == null ? other$value != null
                           : !this$value.equals(other$value))
      return false;
    return true;
  }

  @Override
  protected boolean canEqual(final Object other) {
    return other instanceof ParameterizedString;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = super.hashCode();
    final Object $value = this.value();
    result =
      (result * PRIME)
        + ($value == null ? 43
                          : $value.hashCode());
    return result;
  }

  public static ParameterizedString of(String value) {
    return new ParameterizedString(value);
  }

}
