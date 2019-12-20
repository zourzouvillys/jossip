/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import static io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RfcSerializationConstants.DQUOT;
import static io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RfcSerializationConstants.GT;
import static io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RfcSerializationConstants.LT;
import static io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RfcSerializationConstants.SEMI;
import static io.rtcore.sip.message.processor.rfc3261.serializing.serializers.RfcSerializationConstants.SP;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * 
 * 
 */
public class NameAddrSerializer extends AbstractRfcSerializer<NameAddr> {

  private final RfcSerializerManager manager;

  public NameAddrSerializer(final RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer w, final NameAddr obj) throws IOException {

    if (obj.getName().isPresent()) {
      w.append(DQUOT);
      w.append(obj.getName().get().replace("\"", "\\\""));
      w.append(DQUOT);
      w.append(SP);
    }

    w.append(LT);
    w.append(this.manager.serialize(obj.address()));
    w.append(GT);

    if (obj.getParameters().isPresent()) {
      w.append(SEMI);
      this.manager.serializeCollection(w, obj.getParameters().get().getRawParameters(), SEMI);
    }

  }

}
