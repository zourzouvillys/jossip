/**
 *
 */
package com.jive.sip.message.api.headers;

import java.io.Serializable;

import lombok.Value;
import lombok.experimental.Wither;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */

@Value
@Wither
public class CallId implements Serializable {

  private final String value;

  public CallId(CharSequence value) {
    this.value = value.toString();
  }

  /**
   * returns the value of this callid, e.g [user@]host
   *
   * @return
   */

  public String getValue() {
    return this.value;
  }

}
