/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.processor.uri.RawUri;

/**
 * 
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
