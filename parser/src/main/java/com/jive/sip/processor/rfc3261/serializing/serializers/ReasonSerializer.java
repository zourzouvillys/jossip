/**
 *
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.message.api.Reason;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * @author theo
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
