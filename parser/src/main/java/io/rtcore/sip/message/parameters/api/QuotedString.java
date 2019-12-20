package io.rtcore.sip.message.parameters.api;

public final class QuotedString {
  private final String value;

  public static QuotedString from(String value) {
    return new QuotedString(value);
  }

  public QuotedString(final String value) {
    this.value = value;
  }

  public String value() {
    return this.value;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof QuotedString)) return false;
    final QuotedString other = (QuotedString) o;
    final Object this$value = this.value();
    final Object other$value = other.value();
    if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $value = this.value();
    result = result * PRIME + ($value == null ? 43 : $value.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "QuotedString(value=" + this.value() + ")";
  }
}
