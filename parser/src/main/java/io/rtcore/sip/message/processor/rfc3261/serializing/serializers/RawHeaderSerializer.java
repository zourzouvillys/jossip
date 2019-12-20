/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.base.api.RawHeader;

/**
 * 
 * 
 */
public class RawHeaderSerializer extends AbstractRfcSerializer<RawHeader> {

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public void serialize(final Writer w, final RawHeader obj) throws IOException {

    w.append(obj.name());

    w.append(RfcSerializationConstants.COLON);
    w.append(RfcSerializationConstants.SP);

    if (obj.value() != null) {
      w.append(obj.value());
    }

  }

}
