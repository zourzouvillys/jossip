package com.jive.sip.processor.rfc3261.serializing.serializers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.jive.sip.base.api.Token;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializer;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManagerBuilder;
import com.jive.sip.uri.TelUri;

public class TelUriSerializerTest {

  private RfcSerializer<TelUri> serializer;

  @BeforeEach
  public void setup() {
    this.serializer = new TelUriSerializer(new RfcSerializerManagerBuilder().build());
  }

  @Test
  public void test() throws IOException {
    final TelUri uri = new TelUri("8019600070");
    final StringWriter w = new StringWriter();
    this.serializer.serialize(w, uri);
    assertEquals("tel:8019600070", w.toString());
  }

  @Test
  public void testWithParam() throws IOException {
    final TelUri uri = new TelUri("8019600070").withParameter(Token.from("xxx")).withParameter(Token.from("yyy"));
    final StringWriter w = new StringWriter();
    this.serializer.serialize(w, uri);
    assertEquals("tel:8019600070;xxx;yyy", w.toString());
  }

}
