/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * 
 * 
 */

public class SipRequestSerializer extends AbstractRfcSerializer<SipRequest> {

  private final RfcSerializerManager manager;

  public SipRequestSerializer(RfcSerializerManager manager) {
    this.manager = manager;
  }

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer#serialize(java.lang.Object)
   */
  @Override
  public void serialize(final Writer writer, final SipRequest obj) throws IOException {

    writer.append(obj.method().getMethod());
    writer.append(RfcSerializationConstants.SP);
    this.manager.serialize(writer, obj.uri());
    writer.append(RfcSerializationConstants.SP);
    writer.append(obj.version());
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
