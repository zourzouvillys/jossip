/**
 *
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.message.api.Replaces;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * @author theo
 *
 */
public class ReplacesSerializer extends AbstractRfcSerializer<Replaces> {

  private final RfcSerializerManager manager;

  public ReplacesSerializer(final RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer sink, final Replaces obj) throws IOException {
    sink.append(obj.callId().getValue());
    if (obj.getParameters().isPresent()) {
      sink.append(RfcSerializationConstants.SEMI);
      this.manager.serializeCollection(sink, obj.getParameters().get().getRawParameters(), RfcSerializationConstants.SEMI);
    }
  }

}
