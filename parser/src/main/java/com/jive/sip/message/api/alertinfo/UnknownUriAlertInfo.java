package com.jive.sip.message.api.alertinfo;

import com.jive.sip.uri.Uri;

public final class UnknownUriAlertInfo implements AlertInfoReference {
  private final Uri uri;

  @Override
  public <T> T apply(final AlertInfoReferenceVisitor<T> visitor) {
    return visitor.visit(this);
  }

  public UnknownUriAlertInfo(final Uri uri) {
    this.uri = uri;
  }

  public Uri uri() {
    return this.uri;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof UnknownUriAlertInfo)) return false;
    final UnknownUriAlertInfo other = (UnknownUriAlertInfo) o;
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
    return "UnknownUriAlertInfo(uri=" + this.uri() + ")";
  }
}
