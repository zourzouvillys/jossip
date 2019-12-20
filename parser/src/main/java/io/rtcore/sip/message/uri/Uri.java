package io.rtcore.sip.message.uri;

/**
 * Base interface for all URIs in the stack.
 * 
 * 
 * 
 */
public interface Uri {

  String getScheme();

  <T> T apply(UriVisitor<T> visitor);

}
