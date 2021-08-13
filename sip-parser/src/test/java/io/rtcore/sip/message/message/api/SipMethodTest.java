package io.rtcore.sip.message.message.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Tests the {@link SipMethod} class.
 * 
 * 
 * 
 */
public class SipMethodTest {

  @Test
  public void test() {

    assertEquals(SipMethod.INVITE, SipMethod.fromString("INVITE"));
    assertEquals(SipMethod.REGISTER, SipMethod.fromString("REGISTER"));
  }

  @Test
  public void testNegativeMatches() {
    // method is case sensitive.
    assertNotEquals(SipMethod.REGISTER, SipMethod.fromString("REGiSTEr"));
    // no trailing or leading spaces
    assertNotEquals(SipMethod.REGISTER, SipMethod.fromString(" REGISTER "));
  }

  @Test
  public void testEquals() {

    assertEquals(SipMethod.fromString("uNkNoWnMetHOD"), SipMethod.fromString("uNkNoWnMetHOD"));

    assertNotEquals(SipMethod.fromString("AnotherUnknownMethod"), SipMethod.fromString("uNkNoWnMetHOD"));

  }

}
