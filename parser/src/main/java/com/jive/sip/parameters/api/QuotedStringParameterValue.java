package com.jive.sip.parameters.api;

public final class QuotedStringParameterValue extends ParameterValue<String> {
  private final String value;

  @Override
  public <T> T apply(ParameterValueVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return new StringBuilder().append('\"').append(value.replaceAll("\"", "\\\"")).append('\"').toString();
  }

  public QuotedStringParameterValue(final String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof QuotedStringParameterValue)) return false;
    final QuotedStringParameterValue other = (QuotedStringParameterValue) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$value = this.value();
    final Object other$value = other.value();
    if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof QuotedStringParameterValue;
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
