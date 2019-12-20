/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import io.rtcore.sip.message.message.api.headers.ParameterizedUri;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * 
 * 
 */
public class ParameterizedUriSerializer extends AbstractRfcSerializer<ParameterizedUri> {

  private final RfcSerializerManager manager;

  public ParameterizedUriSerializer(final RfcSerializerManager manager) {
    this.manager = manager;
  }

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(final ParameterizedUri obj) {
    return "<" + this.manager.serialize(obj.uri()) + ">" + this.manager.serialize(obj.getParameters());
  }

}
