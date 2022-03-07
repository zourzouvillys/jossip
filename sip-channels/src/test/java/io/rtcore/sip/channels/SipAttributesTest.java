package io.rtcore.sip.channels;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.channels.internal.SipAttributes;

class SipAttributesTest {

  @Test
  void test() {
    assertEquals(SipAttributes.of(), SipAttributes.newBuilder().build());
  }

}
