/**
 *
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.message.api.headers.ParameterizedString;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

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
