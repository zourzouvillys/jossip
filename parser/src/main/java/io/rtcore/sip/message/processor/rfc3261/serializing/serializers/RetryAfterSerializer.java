/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import io.rtcore.sip.message.message.api.headers.RetryAfter;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * 
 *
 */
public class RetryAfterSerializer extends AbstractRfcSerializer<RetryAfter> {

  private final RfcSerializerManager manager;

  public RetryAfterSerializer(final RfcSerializerManager manager) {
    this.manager = manager;
  }

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(final RetryAfter obj) {
    String result = obj.delta() + " " + obj.getComment().orElse("");
    result += this.manager.serialize(obj.getParameters());
    return result;
  }

}
