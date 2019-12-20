/**
 * 
 */
package com.jive.sip.uri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 */
public final class UrnService {
  private final String service;

  public String toString() {
    return service;
  }

  public UrnService(final String service) {
    this.service = service;
  }

  public String service() {
    return this.service;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof UrnService)) return false;
    final UrnService other = (UrnService) o;
    final Object this$service = this.service();
    final Object other$service = other.service();
    if (this$service == null ? other$service != null : !this$service.equals(other$service)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $service = this.service();
    result = result * PRIME + ($service == null ? 43 : $service.hashCode());
    return result;
  }
}
