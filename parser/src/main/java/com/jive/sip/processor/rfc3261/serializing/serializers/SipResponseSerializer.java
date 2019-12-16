/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.message.SipResponse;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */

public class SipResponseSerializer extends AbstractRfcSerializer<SipResponse> {

  private final RfcSerializerManager manager;

  public SipResponseSerializer(RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer writer, final SipResponse obj) throws IOException {

    writer.append(obj.version());
    writer.append(RfcSerializationConstants.SP);
    writer.append(Integer.toString(obj.getStatus().code()));
    writer.append(RfcSerializationConstants.SP);
    writer.append(obj.getStatus().reason());
    writer.append(RfcSerializationConstants.CRLF);

    this.manager.serializeCollection(writer, obj.headers(), RfcSerializationConstants.CRLF);

    writer.append(RfcSerializationConstants.CRLF);
    writer.append(RfcSerializationConstants.CRLF);

    if (obj.body() != null) {
      // TODO: urgh, perhaps not Writer?
      writer.write(new String(obj.body()).toCharArray());
    }

  }

}
