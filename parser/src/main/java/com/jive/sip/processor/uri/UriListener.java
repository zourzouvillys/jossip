/**
 * 
 */
package com.jive.sip.processor.uri;

import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.uri.Uri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * @param <T>
 */
public class UriListener<T extends Uri> implements ValueListener<T> {
  private T uri;

  /*
   * (non-Javadoc)
   * @see com.jive.sip.parsers.core.ValueListener#set(java.lang.Object)
   */
  @Override
  public void set(T value) {
    uri = value;
  }

  public T uri() {
    return this.uri;
  }
}
