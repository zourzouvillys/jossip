package com.jive.sip.message.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * Tests the {@link SipMethod} class.
 * 
 * @author theo
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
