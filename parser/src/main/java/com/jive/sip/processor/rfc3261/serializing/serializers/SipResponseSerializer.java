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

    writer.append(obj.getVersion());
    writer.append(RfcSerializationConstants.SP);
    writer.append(Integer.toString(obj.getStatus().getCode()));
    writer.append(RfcSerializationConstants.SP);
    writer.append(obj.getStatus().getReason());
    writer.append(RfcSerializationConstants.CRLF);

    this.manager.serializeCollection(writer, obj.getHeaders(), RfcSerializationConstants.CRLF);

    writer.append(RfcSerializationConstants.CRLF);
    writer.append(RfcSerializationConstants.CRLF);

    if (obj.getBody() != null) {
      // TODO: urgh, perhaps not Writer?
      writer.write(new String(obj.getBody()).toCharArray());
    }

  }

}
