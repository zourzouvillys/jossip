package com.jive.sip.processor.rfc3261.serializing.serializers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.jive.sip.message.api.TokenSet;
import com.jive.sip.processor.rfc3261.RfcSipMessageManagerBuilder;
import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManagerBuilder;

public class RfcSerializerManagerTest {

  private SipMessageManager manager;
  private RfcSerializerManager serializer;

  @BeforeEach
  public void setup() {
    this.manager = new RfcSipMessageManagerBuilder().build();
    this.serializer = new RfcSerializerManagerBuilder().build();
  }

  @Test
  public void test() throws IOException {
    final TokenSet set = TokenSet.fromList(Lists.newArrayList("alice", "bob"));
    final StringWriter w = new StringWriter();
    this.serializer.serialize(w, set);
    assertEquals("alice, bob", w.toString());
  }

}
