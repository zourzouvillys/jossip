package io.rtcore.sip.message.uri;

public final class HttpUri implements Uri {
  private final boolean secure;
  private final String opaque;

  @Override
  public String getScheme() {
    return secure ? "https" : "http";
  }

  public String toString() {
    return String.format("%s:%s", getScheme(), opaque);
  }

  @Override
  public <T> T apply(UriVisitor<T> visitor) {
    if (visitor instanceof HttpUriVisitor<?>) {
      return ((HttpUriVisitor<T>) visitor).visit(this);
    }
    return visitor.visit(this);
  }

  public static Uri secure(String hostAndPath) {
    return new HttpUri(true, String.format("//%s", hostAndPath));
  }

  public static Uri insecure(String hostAndPath) {
    return new HttpUri(false, String.format("//%s", hostAndPath));
  }

  public HttpUri(final boolean secure, final String opaque) {
    this.secure = secure;
    this.opaque = opaque;
  }

  public boolean secure() {
    return this.secure;
  }

  public String opaque() {
    return this.opaque;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof HttpUri)) return false;
    final HttpUri other = (HttpUri) o;
    if (this.secure() != other.secure()) return false;
    final Object this$opaque = this.opaque();
    final Object other$opaque = other.opaque();
    if (this$opaque == null ? other$opaque != null : !this$opaque.equals(other$opaque)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    result = result * PRIME + (this.secure() ? 79 : 97);
    final Object $opaque = this.opaque();
    result = result * PRIME + ($opaque == null ? 43 : $opaque.hashCode());
    return result;
  }
}
