package io.rtcore.sip.message.message.api.alertinfo;

public final class HttpUriAlertInfo implements AlertInfoReference {
  private final String uri;

  @Override
  public <T> T apply(final AlertInfoReferenceVisitor<T> visitor) {
    return visitor.visit(this);
  }

  public HttpUriAlertInfo(final String uri) {
    this.uri = uri;
  }

  public String uri() {
    return this.uri;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof HttpUriAlertInfo)) return false;
    final HttpUriAlertInfo other = (HttpUriAlertInfo) o;
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
    return "HttpUriAlertInfo(uri=" + this.uri() + ")";
  }
}
