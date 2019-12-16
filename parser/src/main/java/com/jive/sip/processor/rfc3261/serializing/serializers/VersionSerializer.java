/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.message.api.headers.Version;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class VersionSerializer extends AbstractRfcSerializer<Version> {

  /*
   * (non-Javadoc)
   * @see com.jive.sip.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */

  @Override
  public void serialize(final Writer w, final Version obj) throws IOException {
    w.append(Integer.toString(obj.majorVersion()));
    w.append(RfcSerializationConstants.DOT);
    w.append(Integer.toString(obj.minorVersion()));
  }

}
