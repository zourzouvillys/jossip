/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import com.jive.sip.message.api.headers.MIMEType;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */

public class MIMETypeSerializer extends AbstractRfcSerializer<MIMEType> {

  private final RfcSerializerManager manager;

  public MIMETypeSerializer(final RfcSerializerManager manager) {
    this.manager = manager;
  }

  /*
   * (non-Javadoc)
   * @see com.jive.sip.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
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
