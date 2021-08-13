package io.rtcore.sip.message.message.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SipTransportTest {

  @Test
  public void test() {
    assertEquals(SipTransport.UDP, SipTransport.fromString("UDP"));
    assertEquals(SipTransport.UDP, SipTransport.fromString("uDp"));
    assertEquals(SipTransport.UDP, SipTransport.fromString("udp"));
    assertTrue(SipTransport.UDP == SipTransport.fromString("UDP"));
    assertTrue(SipTransport.UDP == SipTransport.fromString("UdP"));
    assertNotEquals(SipTransport.UDP, SipTransport.fromString("TCP"));
    assertNotEquals(SipTransport.UDP, SipTransport.fromString("uup"));
    assertNotEquals(SipTransport.UDP, SipTransport.fromString("not-udp"));
  }

}
