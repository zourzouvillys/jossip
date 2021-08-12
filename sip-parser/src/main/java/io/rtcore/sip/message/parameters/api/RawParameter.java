package io.rtcore.sip.message.parameters.api;

import com.google.common.base.Strings;

import io.rtcore.sip.message.base.api.Token;

public final class RawParameter {
  private final Token name;
  private final ParameterValue<?> value;

  /**
   * A new RawParameter flag (without a value).
   * 
   * @param name
   */
  public RawParameter(CharSequence name) {
    this.name = Token.from(name);
    this.value = FlagParameterValue.getInstance();
  }

  public RawParameter(Token name, ParameterValue<?> value) {
    this.name = name;
    this.value = value;
  }

  public RawParameter(CharSequence name, ParameterValue<?> value) {
    this.name = Token.from(name);
    this.value = value;
  }

  public static RawParameter of(Token name, Token value) {
    return new RawParameter(name, new TokenParameterValue(value));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    if (!Strings.isNullOrEmpty(this.value.toString())) {
      sb.append('=').append(value);
    }
    return sb.toString();
  }

  public Token name() {
    return this.name;
  }

  public ParameterValue<?> value() {
    return this.value;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof RawParameter))
      return false;
    final RawParameter other = (RawParameter) o;
    final Object this$name = this.name();
    final Object other$name = other.name();
    if (this$name == null ? other$name != null
                          : !this$name.equals(other$name))
      return false;
    final Object this$value = this.value();
    final Object other$value = other.value();
    if (this$value == null ? other$value != null
                           : !this$value.equals(other$value))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $name = this.name();
    result =
      (result * PRIME)
        + ($name == null ? 43
                         : $name.hashCode());
    final Object $value = this.value();
    result =
      (result * PRIME)
        + ($value == null ? 43
                          : $value.hashCode());
    return result;
  }

}
