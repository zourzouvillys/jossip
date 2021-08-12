/**
 * 
 */
package io.rtcore.sip.message.uri;

/**
 * 
 *
 */
public interface TelUriVisitor<T> extends UriVisitor<T> {
  public T visit(final TelUri uri);
}
