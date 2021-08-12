/**
 * 
 */
package io.rtcore.sip.message.uri;

/**
 * 
 *
 */
public interface UriVisitor<T> {
  public T visit(final Uri unknown);
}
