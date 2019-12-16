package com.jive.sip.uri;

/**
 * Base interface for all URIs in the stack.
 * 
 * @author theo
 * 
 */
public interface Uri {
  String getScheme();

  <T> T apply(UriVisitor<T> visitor);

}
