package com.jive.sip.uri.api;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * Matches a URN based URI, e.g rfc5031
 * 
 * Todo: we probably want to add "sub" parsers for the URNs.
 * 
 */

@Value
@EqualsAndHashCode
public class UrnUri implements Uri {
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

}
