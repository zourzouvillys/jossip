package com.jive.sip.processor.rfc3261.serializing;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.message.api.Via;
import com.jive.sip.processor.rfc3261.serializing.serializers.AbstractRfcSerializer;
import com.jive.sip.processor.rfc3261.serializing.serializers.RfcSerializationConstants;

public class ViaSerializer extends AbstractRfcSerializer<Via> {

  private final RfcSerializerManager manager;

  ViaSerializer(RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer writer, final Via obj) throws IOException {

    writer.append(obj.getProtocol().getName());
    writer.append(RfcSerializationConstants.SLASH);
    writer.append(obj.getProtocol().getVersion());
    writer.append(RfcSerializationConstants.SLASH);
    writer.append(obj.getProtocol().getTransport());

    writer.append(RfcSerializationConstants.SP);
    writer.append(obj.getSentBy().toString());

    if (obj.getParameters().isPresent()) {
      writer.append(RfcSerializationConstants.SEMI);
      this.manager.serializeCollection(writer, obj.getParameters().get().getRawParameters(), RfcSerializationConstants.SEMI);
    }

  }

}
