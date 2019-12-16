package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.message.api.ContentDisposition;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

public class ContentDispositionSerializer extends AbstractRfcSerializer<ContentDisposition> {

  private RfcSerializerManager manager;

  public ContentDispositionSerializer(RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer sink, final ContentDisposition obj) throws IOException {

    sink.append(obj.getValue());

    if (obj.getParameters().isPresent()) {
      sink.append(RfcSerializationConstants.SEMI);
      manager.serializeCollection(sink,
        obj.getParameters().get().getRawParameters(),
        RfcSerializationConstants.SEMI);
    }

  }

}
