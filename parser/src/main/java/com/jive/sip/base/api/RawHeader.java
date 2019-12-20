package com.jive.sip.base.api;

/**
 * Representation of a single header in a SIP message.
 */
public final class RawHeader {
  // * NOTE: this header is expected to be immutable. do NOT add any methods which mutate the value.
  private final String name;
  private final String value;

  public RawHeader(final String name, final String value) {
    this.name = name;
    this.value = value;
  }

  public String name() {
    return this.name;
  }

  public String value() {
    return this.value;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof RawHeader)) return false;
    final RawHeader other = (RawHeader) o;
    final Object this$name = this.name();
    final Object other$name = other.name();
    if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
    final Object this$value = this.value();
    final Object other$value = other.value();
    if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $name = this.name();
    result = result * PRIME + ($name == null ? 43 : $name.hashCode());
    final Object $value = this.value();
    result = result * PRIME + ($value == null ? 43 : $value.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "RawHeader(name=" + this.name() + ", value=" + this.value() + ")";
  }
}
