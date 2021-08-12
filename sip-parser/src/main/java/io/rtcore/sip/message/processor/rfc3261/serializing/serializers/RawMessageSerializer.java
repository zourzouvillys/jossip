/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import io.rtcore.sip.message.base.api.RawMessage;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * 
 * 
 */
public class RawMessageSerializer extends AbstractRfcSerializer<RawMessage> {

  private final RfcSerializerManager manager;

  public RawMessageSerializer(RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer w, final RawMessage msg) throws IOException {

    w.append(msg.getInitialLine());
    w.append(RfcSerializationConstants.CRLF);

    manager.serializeCollection(w, msg.getHeaders(), RfcSerializationConstants.CRLF);
    w.append(RfcSerializationConstants.CRLF);
    w.append(RfcSerializationConstants.CRLF);

    if (msg.getContentLength().isPresent()) {
      w.append(new String(msg.getBody(), StandardCharsets.UTF_8));
    }

  }

}
