/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import io.rtcore.sip.message.message.api.headers.MIMEType;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * 
 * 
 */

public class MIMETypeSerializer extends AbstractRfcSerializer<MIMEType> {

  private final RfcSerializerManager manager;

  public MIMETypeSerializer(final RfcSerializerManager manager) {
    this.manager = manager;
  }

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(final MIMEType obj) {
    return obj.type()
      + "/"
      + obj.subType()
      + (obj.getParameters().isPresent() ? ";"
        + this.manager
          .serialize(obj.getParameters())
                                         : "");
  }

}
