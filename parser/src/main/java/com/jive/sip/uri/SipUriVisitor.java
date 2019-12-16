/**
 * 
 */
package com.jive.sip.uri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public interface SipUriVisitor<T> extends UriVisitor<T> {
  public T visit(final SipUri uri);
}
