/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.message.api.headers.Version;

/**
 * 
 * 
 */
public class VersionSerializer extends AbstractRfcSerializer<Version> {

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */

  @Override
  public void serialize(final Writer w, final Version obj) throws IOException {
    w.append(Integer.toString(obj.majorVersion()));
    w.append(RfcSerializationConstants.DOT);
    w.append(Integer.toString(obj.minorVersion()));
  }

}
