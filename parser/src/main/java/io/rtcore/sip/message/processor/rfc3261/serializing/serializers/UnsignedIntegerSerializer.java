/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.google.common.primitives.UnsignedInteger;

/**
 * 
 * 
 */
public class UnsignedIntegerSerializer extends AbstractRfcSerializer<UnsignedInteger> {

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */

  @Override
  public void serialize(final Writer w, final UnsignedInteger obj) throws IOException {
    w.append(obj.toString());
  }
}
