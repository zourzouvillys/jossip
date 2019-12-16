/**
 * 
 */
package com.jive.sip.message.api.headers;

import lombok.Value;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
@Value
public class Version {
  private final int majorVersion;
  private final int minorVersion;
}
