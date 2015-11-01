/**
 * 
 */
package com.jive.sip.uri.api;

import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
@Value
@EqualsAndHashCode
public class UrnService
{
  private final String service;
  
  public String toString()
  {
    return service;
  }
}
