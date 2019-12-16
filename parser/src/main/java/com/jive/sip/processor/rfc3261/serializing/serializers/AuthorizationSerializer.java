/**
 * 
 */
package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.auth.headers.Authorization;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

import lombok.AllArgsConstructor;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
@AllArgsConstructor
public class AuthorizationSerializer extends AbstractRfcSerializer<Authorization> {

  private final RfcSerializerManager manager;

  @Override
  public void serialize(final Writer w, final Authorization obj) throws IOException {

    w.append(obj.getScheme()).append(' ');

    if (obj.getParameters().isPresent()) {
      this.manager.serializeCollection(w, obj.getParameters().get().getRawParameters(), ", ");
    }

  }

}
