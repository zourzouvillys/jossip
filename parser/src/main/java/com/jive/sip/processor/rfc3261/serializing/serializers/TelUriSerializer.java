/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;
import com.jive.sip.uri.api.TelUri;

import lombok.AllArgsConstructor;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
@AllArgsConstructor
public class TelUriSerializer extends AbstractRfcSerializer<TelUri> {

  private final RfcSerializerManager manager;

  /*
   * (non-Javadoc)
   * @see com.jive.sip.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */

  @Override
  public void serialize(final Writer w, final TelUri obj) throws IOException {
    w.append(obj.getScheme());
    w.append(RfcSerializationConstants.COLON);
    w.append(obj.getNumber());

    if (obj.getParameters().isPresent()) {
      w.append(RfcSerializationConstants.SEMI);
      this.manager.serializeCollection(w, obj.getParameters().get().getRawParameters(), RfcSerializationConstants.SEMI);
    }

  }

}
