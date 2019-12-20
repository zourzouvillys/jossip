package io.rtcore.sip.message.message.api.alertinfo;

import io.rtcore.sip.message.uri.UrnUri;

public final class WellKnownAlertInfo implements AlertInfoReference {
  private final UrnUri uri;

  public WellKnownAlertInfo(final UrnUri uri) {
    this.uri = uri;
  }

  @Override
  public <T> T apply(final AlertInfoReferenceVisitor<T> visitor) {
    return visitor.visit(this);
  }

  public UrnUri uri() {
    return this.uri;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof WellKnownAlertInfo)) return false;
    final WellKnownAlertInfo other = (WellKnownAlertInfo) o;
    final Object this$uri = this.uri();
    final Object other$uri = other.uri();
    if (this$uri == null ? other$uri != null : !this$uri.equals(other$uri)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $uri = this.uri();
    result = result * PRIME + ($uri == null ? 43 : $uri.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "WellKnownAlertInfo(uri=" + this.uri() + ")";
  }
}
