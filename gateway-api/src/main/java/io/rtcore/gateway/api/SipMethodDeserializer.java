package io.rtcore.gateway.api;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;

import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.common.iana.SipMethods;

public class SipMethodDeserializer extends StdNodeBasedDeserializer<SipMethodId> {

  private static final long serialVersionUID = 1L;

  public SipMethodDeserializer() {
    super(SipMethodId.class);
  }

  @Override
  public SipMethodId convert(JsonNode root, DeserializationContext ctxt) throws IOException {
    return SipMethods.toMethodId(root.asText());
  }

}
