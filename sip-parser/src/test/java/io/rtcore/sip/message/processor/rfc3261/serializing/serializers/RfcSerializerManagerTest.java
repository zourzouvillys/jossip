package io.rtcore.sip.message.processor.rfc3261.serializing.serializers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.message.api.TokenSet;
import io.rtcore.sip.message.processor.rfc3261.RfcSipMessageManagerBuilder;
import io.rtcore.sip.message.processor.rfc3261.SipMessageManager;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManager;
import io.rtcore.sip.message.processor.rfc3261.serializing.RfcSerializerManagerBuilder;

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
