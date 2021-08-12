package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.message.api.ContentDisposition;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

public class ContentDispositionSerializer extends AbstractRfcSerializer<ContentDisposition> {

  private RfcSerializerManager manager;

  public ContentDispositionSerializer(RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer sink, final ContentDisposition obj) throws IOException {

    sink.append(obj.value());

    if (obj.getParameters().isPresent()) {
      sink.append(RfcSerializationConstants.SEMI);
      manager.serializeCollection(sink,
        obj.getParameters().get().getRawParameters(),
        RfcSerializationConstants.SEMI);
    }

  }

}
