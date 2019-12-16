/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.processor.uri.RawUri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class RawUriSerializer extends AbstractRfcSerializer<RawUri> {

  @Override
  public void serialize(final Writer w, final RawUri obj) throws IOException {
    w.append(obj.getScheme());
    w.append(RfcSerializationConstants.COLON);
    w.append(obj.getOpaque());
  }

}
