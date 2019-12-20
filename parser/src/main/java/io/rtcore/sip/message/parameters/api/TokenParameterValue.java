package io.rtcore.sip.message.parameters.api;

import com.google.common.base.Preconditions;

import io.rtcore.sip.message.base.api.Token;

public final class TokenParameterValue extends ParameterValue<Token> {
  private final Token value;

  public TokenParameterValue(Token value) {
    this.value = Preconditions.checkNotNull(value);
  }

  public TokenParameterValue(String value) {
    this.value = Token.from(value);
  }

  public TokenParameterValue(long value) {
    this.value = Token.from(Long.toString(value));
  }

  @Override
  public <T> T apply(ParameterValueVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return value.toString();
  }

  public Token value() {
    return this.value;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof TokenParameterValue)) return false;
    final TokenParameterValue other = (TokenParameterValue) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$value = this.value();
    final Object other$value = other.value();
    if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof TokenParameterValue;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $value = this.value();
    result = result * PRIME + ($value == null ? 43 : $value.hashCode());
    return result;
  }
}
