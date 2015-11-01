/**
 * 
 */
package com.jive.sip.message.api.headers;

import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.experimental.Wither;

/**
 * 
 */

@Value
@Wither
@EqualsAndHashCode
public class RValue
{

  private final CharSequence namespace;
  private final CharSequence priority;

  public RValue(CharSequence namespace, CharSequence priority)
  {
    this.namespace = namespace;
    this.priority = priority;
  }


}
