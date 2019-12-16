package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.message.api.MinSE;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

public class MinSESerializer extends AbstractRfcSerializer<MinSE> {

  private RfcSerializerManager manager;

  public MinSESerializer(RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer sink, final MinSE obj) throws IOException {
    sink.append(Long.toString(obj.duration().getSeconds()));
    if (obj.getParameters().isPresent()) {
      sink.append(RfcSerializationConstants.SEMI);
      manager.serializeCollection(
        sink,
        obj.getParameters().get().getRawParameters(),
        RfcSerializationConstants.SEMI);
    }
  }

}
