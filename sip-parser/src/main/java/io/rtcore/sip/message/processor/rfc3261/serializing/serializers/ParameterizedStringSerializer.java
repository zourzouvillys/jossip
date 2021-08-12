/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.message.api.headers.ParameterizedString;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 */

public class ParameterizedStringSerializer extends AbstractRfcSerializer<ParameterizedString> {

  private final RfcSerializerManager manager;

  public ParameterizedStringSerializer(RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer sink, final ParameterizedString obj) throws IOException {
    sink.write(obj.value());
    Parameters params = obj.getParameters().orElse(null);
    if (params == null)
      return;
    sink.append(RfcSerializationConstants.SEMI);
    this.manager.serializeCollection(sink, params.getRawParameters(), RfcSerializationConstants.SEMI);
  }

}
