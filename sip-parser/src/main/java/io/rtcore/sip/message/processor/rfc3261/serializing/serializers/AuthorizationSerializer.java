/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;

/**
 * 
 * 
 */

public class AuthorizationSerializer extends AbstractRfcSerializer<Authorization> {

  private final RfcSerializerManager manager;

  public AuthorizationSerializer(RfcSerializerManager manager) {
    this.manager = manager;
  }

  @Override
  public void serialize(final Writer w, final Authorization obj) throws IOException {

    w.append(obj.scheme()).append(' ');

    if (obj.getParameters().isPresent()) {
      this.manager.serializeCollection(w, obj.getParameters().get().getRawParameters(), ", ");
    }

  }

}
