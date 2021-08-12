package io.rtcore.sip.channels;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SipAttributesTest {

  @Test
  void test() {
    assertEquals(SipAttributes.of(), SipAttributes.newBuilder().build());
  }

}
