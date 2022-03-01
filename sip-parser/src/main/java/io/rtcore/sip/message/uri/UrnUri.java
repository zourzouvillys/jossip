package io.rtcore.sip.message.uri;

import java.net.URI;

/**
 * Matches a URN based URI, e.g rfc5031
 * 
 * Todo: we probably want to add "sub" parsers for the URNs.
 */
public final class UrnUri implements Uri {
  public static final String SERVICE = "service";
  private final String scheme;
  private final UrnService service;

  @Override
  public String getScheme() {
    return this.scheme;
  }

  @Override
  public String toString() {
    return "urn:" + this.scheme + ":" + service.toString();
  }

  @Override
  public <T> T apply(UriVisitor<T> visitor) {
    if (visitor instanceof UrnUriVisitor<?>) {
      return ((UrnUriVisitor<T>) visitor).visit(this);
    }
    return visitor.visit(this);
  }

  public UrnUri(final String scheme, final UrnService service) {
    this.scheme = scheme;
    this.service = service;
  }

  public String scheme() {
    return this.scheme;
  }

  public UrnService service() {
    return this.service;
  }

  @Override
  public URI uri() {
    return URI.create(this.toString());
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this)
      return true;
    if (!(o instanceof UrnUri))
      return false;
    final UrnUri other = (UrnUri) o;
    final Object this$scheme = this.scheme();
    final Object other$scheme = other.scheme();
    if (this$scheme == null ? other$scheme != null
                            : !this$scheme.equals(other$scheme))
      return false;
    final Object this$service = this.service();
    final Object other$service = other.service();
    if (this$service == null ? other$service != null
                             : !this$service.equals(other$service))
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $scheme = this.scheme();
    result =
      result * PRIME
        + ($scheme == null ? 43
                           : $scheme.hashCode());
    final Object $service = this.service();
    result =
      result * PRIME
        + ($service == null ? 43
                            : $service.hashCode());
    return result;
  }
}
