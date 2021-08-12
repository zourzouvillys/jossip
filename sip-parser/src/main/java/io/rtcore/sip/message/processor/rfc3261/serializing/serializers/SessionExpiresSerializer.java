package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.message.api.SessionExpires;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

public class SessionExpiresSerializer extends AbstractRfcSerializer<SessionExpires> {

  private RfcSerializerManager manager;

  public SessionExpiresSerializer(RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer sink, final SessionExpires obj) throws IOException {
    sink.append(Long.toString(obj.duration()));
    if (obj.getParameters().isPresent()) {
      sink.append(RfcSerializationConstants.SEMI);
      manager.serializeCollection(
        sink,
        obj.getParameters().get().getRawParameters(),
        RfcSerializationConstants.SEMI);
    }
  }

}
