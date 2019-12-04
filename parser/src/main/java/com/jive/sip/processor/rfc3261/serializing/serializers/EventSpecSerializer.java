package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.Writer;

import com.jive.sip.message.api.EventSpec;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventSpecSerializer extends AbstractRfcSerializer<EventSpec>
{

  private final RfcSerializerManager manager;

  @Override
  public void serialize(final Writer w, final EventSpec obj) throws IOException
  {

    w.append(obj.getName());

    if (obj.getParameters().isPresent())
    {
      w.append(RfcSerializationConstants.SEMI);
      this.manager.serializeCollection(w, obj.getParameters().get().getRawParameters(), RfcSerializationConstants.SEMI);
    }

  }

}
