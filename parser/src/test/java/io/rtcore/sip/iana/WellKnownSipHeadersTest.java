package io.rtcore.sip.iana;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.iana.WellKnownSipHeaders;

class WellKnownSipHeadersTest {

  @Test
  void test() {

    WellKnownSipHeaders.forEach(hdr -> assertTrue(hdr.headerId() != null));

    assertEquals(WellKnownSipHeaders.VIA, WellKnownSipHeaders.fromString("vIa"));
    assertEquals(WellKnownSipHeaders.VIA, WellKnownSipHeaders.fromString("VIA"));
    assertEquals(WellKnownSipHeaders.VIA, WellKnownSipHeaders.fromString("v"));
    assertEquals(WellKnownSipHeaders.VIA, WellKnownSipHeaders.fromString("V"));

  }

}
