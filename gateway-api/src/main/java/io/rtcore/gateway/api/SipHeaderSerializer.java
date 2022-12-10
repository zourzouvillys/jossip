package io.rtcore.gateway.api;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.rtcore.sip.common.SipHeaders;

public class SipHeaderSerializer extends StdSerializer<SipHeaders> {

  public SipHeaderSerializer() {
    super(SipHeaders.class);
  }

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public void serialize(SipHeaders value, JsonGenerator gen, SerializerProvider provider) throws IOException {

    gen.writeStartObject();

    value.headers().forEachOrdered(h -> {

      try {

        gen.writeFieldName(h.prettyName().toLowerCase());
        gen.writeStartArray();

        value.get(h).forEachOrdered(val -> {

          try {
            gen.writeString(val);
          }
          catch (IOException e) {
            throw new UncheckedIOException(e);
          }

        });

        gen.writeEndArray();

      }
      catch (IOException e) {
        throw new UncheckedIOException(e);
      }

    });

    gen.writeEndObject();

  }

}
