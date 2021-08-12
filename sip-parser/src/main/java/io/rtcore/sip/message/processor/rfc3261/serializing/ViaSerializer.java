package io.rtcore.sip.message.processor.rfc3261.serializing;

import static io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RfcSerializationConstants.COLON;
import static io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RfcSerializationConstants.SEMI;
import static io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RfcSerializationConstants.SLASH;
import static io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RfcSerializationConstants.SP;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.processor.rfc3261.serializing.serializers.AbstractRfcSerializer;

public class ViaSerializer extends AbstractRfcSerializer<Via> {

  private final RfcSerializerManager manager;

  ViaSerializer(final RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer writer, final Via obj) throws IOException {

    writer.append(obj.protocol().name());
    writer.append(SLASH);
    writer.append(obj.protocol().version());
    writer.append(SLASH);
    writer.append(obj.protocol().transport());

    writer.append(SP);

    writer.append(obj.sentBy().host().toUriString());

    if (obj.sentBy().port().isPresent()) {
      writer.append(COLON).append(Integer.toString(obj.sentBy().port().getAsInt()));
    }

    obj
    .getParameters()
    .filter(Parameters::isNotEmpty)
    .ifPresent(p -> p.encodeTo(this.manager, writer, SEMI, SEMI));

  }

}
