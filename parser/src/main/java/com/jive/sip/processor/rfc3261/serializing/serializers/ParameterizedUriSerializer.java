/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import com.jive.sip.message.api.headers.ParameterizedUri;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class ParameterizedUriSerializer extends AbstractRfcSerializer<ParameterizedUri> {

  private final RfcSerializerManager manager;

  public ParameterizedUriSerializer(final RfcSerializerManager manager) {
    this.manager = manager;
  }

  /*
   * (non-Javadoc)
   * @see com.jive.sip.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(final ParameterizedUri obj) {
    return "<" + this.manager.serialize(obj.uri()) + ">" + this.manager.serialize(obj.getParameters());
  }

}
