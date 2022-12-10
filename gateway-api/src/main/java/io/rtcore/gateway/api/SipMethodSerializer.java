package io.rtcore.gateway.api;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import io.rtcore.sip.common.iana.SipMethodId;

public class SipMethodSerializer extends StdSerializer<SipMethodId> {

  private static final long serialVersionUID = 1L;

  public SipMethodSerializer() {
    super(SipMethodId.class);
  }

  @Override
  public void serialize(SipMethodId value, JsonGenerator gen, SerializerProvider provider) throws IOException {
    gen.writeString(value.token());
  }

}
