/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.message.api.Reason;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * 
 *
 */
public class ReasonSerializer extends AbstractRfcSerializer<Reason> {

  private final RfcSerializerManager manager;

  public ReasonSerializer(final RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer sink, final Reason obj) throws IOException {
    sink.append(obj.protocol());
    if (obj.getParameters().isPresent()) {
      sink.append(RfcSerializationConstants.SEMI);
      this.manager.serializeCollection(sink, obj.getParameters().get().getRawParameters(), RfcSerializationConstants.SEMI);
    }
  }

}
