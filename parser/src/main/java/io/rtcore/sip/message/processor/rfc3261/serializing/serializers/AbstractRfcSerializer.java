package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializer;

public abstract class AbstractRfcSerializer<T> implements RfcSerializer<T> {

  @Override
  public void serialize(final Writer sink, final T obj) throws IOException {
    final String output = serialize(obj);
    sink.append(output);
  }

  @Override
  public String serialize(final T obj) {
    final StringWriter writer = new StringWriter();
    try {
      serialize(writer, obj);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    return writer.toString();
  }

}
