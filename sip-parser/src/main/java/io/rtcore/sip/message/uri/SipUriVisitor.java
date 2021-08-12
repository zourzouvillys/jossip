/**
 * 
 */
package io.rtcore.sip.message.uri;

/**
 * 
 *
 */
public interface SipUriVisitor<T> extends UriVisitor<T> {
  public T visit(final SipUri uri);
}
