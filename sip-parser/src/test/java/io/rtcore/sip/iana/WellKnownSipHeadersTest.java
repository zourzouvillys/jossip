package io.rtcore.sip.iana;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.common.iana.StandardSipHeaders;

class WellKnownSipHeadersTest {

  @Test
  void test() {

    StandardSipHeaders.forEach(hdr -> assertTrue(hdr.headerId() != null));

    assertEquals(StandardSipHeaders.VIA, StandardSipHeaders.fromString("vIa"));
    assertEquals(StandardSipHeaders.VIA, StandardSipHeaders.fromString("VIA"));
    assertEquals(StandardSipHeaders.VIA, StandardSipHeaders.fromString("v"));
    assertEquals(StandardSipHeaders.VIA, StandardSipHeaders.fromString("V"));

  }

}
