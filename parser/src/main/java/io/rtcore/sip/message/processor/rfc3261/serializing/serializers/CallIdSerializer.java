/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import io.rtcore.sip.message.message.api.headers.CallId;

/**
 * 
 * 
 */
public class CallIdSerializer extends AbstractRfcSerializer<CallId> {

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(final CallId obj) {
    return obj.getValue();
  }

}
