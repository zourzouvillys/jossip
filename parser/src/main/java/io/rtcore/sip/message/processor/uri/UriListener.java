/**
 * 
 */
package io.rtcore.sip.message.processor.uri;

import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.uri.Uri;

/**
 * 
 * @param <T>
 */
public class UriListener<T extends Uri> implements ValueListener<T> {
  private T uri;

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.parsers.core.ValueListener#set(java.lang.Object)
   */
  @Override
  public void set(T value) {
    uri = value;
  }

  public T uri() {
    return this.uri;
  }
}
