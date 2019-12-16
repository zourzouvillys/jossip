/**
 * 
 */
package com.jive.sip.uri.api;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public interface UriVisitor<T> {
  public T visit(final Uri unknown);
}
