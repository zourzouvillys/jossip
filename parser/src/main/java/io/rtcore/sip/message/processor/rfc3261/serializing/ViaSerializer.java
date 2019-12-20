package io.rtcore.sip.message.processor.rfc3261.serializing;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.AbstractRfcSerializer;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RfcSerializationConstants;

public class ViaSerializer extends AbstractRfcSerializer<Via> {

  private final RfcSerializerManager manager;

  ViaSerializer(RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer writer, final Via obj) throws IOException {

    writer.append(obj.protocol().name());
    writer.append(RfcSerializationConstants.SLASH);
    writer.append(obj.protocol().version());
    writer.append(RfcSerializationConstants.SLASH);
    writer.append(obj.protocol().transport());

    writer.append(RfcSerializationConstants.SP);
    writer.append(obj.sentBy().toString());

    if (obj.getParameters().isPresent()) {
      writer.append(RfcSerializationConstants.SEMI);
      this.manager.serializeCollection(writer, obj.getParameters().get().getRawParameters(), RfcSerializationConstants.SEMI);
    }

  }

}
