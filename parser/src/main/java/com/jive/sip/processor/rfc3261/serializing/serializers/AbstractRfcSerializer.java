package com.jive.sip.processor.rfc3261.serializing.serializers;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import com.jive.sip.processor.rfc3261.serializing.RfcSerializer;

import lombok.SneakyThrows;

public abstract class AbstractRfcSerializer<T> implements RfcSerializer<T> {

  @Override
  public void serialize(final Writer sink, final T obj) throws IOException {
    final String output = serialize(obj);
    sink.append(output);
  }

  @SneakyThrows
  @Override
  public String serialize(final T obj) {
    final StringWriter writer = new StringWriter();
    serialize(writer, obj);
    return writer.toString();
  }

}
