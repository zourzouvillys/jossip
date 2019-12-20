package com.jive.sip.parameters.api;

import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;

public final class HostAndPortParameterValue extends ParameterValue<HostAndPort> {
  private final HostAndPort value;

  public HostAndPortParameterValue(String value) {
    Preconditions.checkNotNull(value);
    this.value = HostAndPort.fromString(value);
  }

  public HostAndPortParameterValue(HostAndPort value) {
    this.value = value;
  }

  @Override
  public <T> T apply(ParameterValueVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return value.toString();
  }

  public HostAndPort value() {
    return this.value;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof HostAndPortParameterValue)) return false;
    final HostAndPortParameterValue other = (HostAndPortParameterValue) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$value = this.value();
    final Object other$value = other.value();
    if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
    return true;
  }

  protected boolean canEqual(final Object other) {
    return other instanceof HostAndPortParameterValue;
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
