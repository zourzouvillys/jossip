package io.rtcore.gateway.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdNodeBasedDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.SipHeaders;

public class SipHeaderDeserializer extends StdNodeBasedDeserializer<SipHeaders> {

  private static final long serialVersionUID = 1L;

  public SipHeaderDeserializer() {
    super(SipHeaders.class);
  }

  @Override
  public SipHeaders convert(JsonNode root, DeserializationContext ctxt) throws IOException {
    List<SipHeaderLine> headers = new ArrayList<>();
    applyValue(headers, root);
    return SipHeaders.of(headers);
  }

  private void applyValue(List<SipHeaderLine> headers, JsonNode root) {
    if (root.isObject()) {
      addHeaders(headers, (ObjectNode) root);
    }
    else if (root.isArray()) {
      addHeaders(headers, (ArrayNode) root);
    }
  }

  private void addHeaders(List<SipHeaderLine> headers, ArrayNode root) {
    for (JsonNode item : root) {
      applyValue(headers, item);
    }
  }

  private void addHeaders(List<SipHeaderLine> headers, ObjectNode root) {

    Iterator<Entry<String, JsonNode>> it = root.fields();

    while (it.hasNext()) {

      Entry<String, JsonNode> e = it.next();
      JsonNode val = e.getValue();

      if (val.isArray()) {
        ((ArrayNode) val).forEach(v -> headers.add(SipHeaderLine.of(e.getKey(), v.asText())));
      }
      else if (val.isValueNode()) {
        headers.add(SipHeaderLine.of(e.getKey(), val.asText()));
      }
      else {
        throw new IllegalArgumentException();
      }

    }

  }

}
