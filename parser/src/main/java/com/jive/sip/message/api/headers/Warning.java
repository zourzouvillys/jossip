/**
 * 
 */
package com.jive.sip.message.api.headers;

import lombok.Getter;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * Warning Field
 */

@Value
@Wither
public class Warning {
  @Getter
  private final int code;
  @Getter
  private final CharSequence agent;
  @Getter
  private final CharSequence text;
}
