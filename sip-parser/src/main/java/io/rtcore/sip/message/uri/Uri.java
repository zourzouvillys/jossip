package io.rtcore.sip.message.uri;

import java.net.URI;

import io.rtcore.sip.common.Address;

/**
 * Base interface for all URIs in the stack.
 */

public interface Uri extends Address {

  String getScheme();

  <T> T apply(UriVisitor<T> visitor);

  @Override
  default String scheme() {
    return this.getScheme();
  }

  URI uri();

}
