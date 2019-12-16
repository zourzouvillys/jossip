/**
 * 
 */
package com.jive.sip.uri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public interface TelUriVisitor<T> extends UriVisitor<T> {
  public T visit(final TelUri uri);
}
