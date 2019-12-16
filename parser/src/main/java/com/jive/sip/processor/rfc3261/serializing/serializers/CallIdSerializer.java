/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import com.jive.sip.message.api.headers.CallId;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class CallIdSerializer extends AbstractRfcSerializer<CallId> {

  /*
   * (non-Javadoc)
   * @see com.jive.sip.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(final CallId obj) {
    return obj.getValue();
  }

}
