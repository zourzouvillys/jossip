/**
 * 
 */
package com.jive.sip.base.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

/**
 * @author Jeff Hutchins {@code <jhutchins@getjive.com>}
 * 
 */
public class RawMessageTest {

  @Test(expected = NullPointerException.class)
  public void testConstructorNull() {
    RawMessage.create(null);
  }

  @Test()
  public void testEqualsWithoutBody() {
    RawMessage msg1 = RawMessage.create("testing");
    RawMessage msg2 = RawMessage.create("testing");
    assertEquals(msg1, msg2);
    assertEquals(msg1.hashCode(), msg2.hashCode());
  }

  @Test()
  public void testEqualsWithBody() {
    RawMessage msg1 = RawMessage.create("testing");
    RawMessage msg2 = RawMessage.create("testing");
    msg1.setBody("testing".getBytes());
    msg2.setBody("testing".getBytes());
    assertEquals(msg1, msg2);
    assertEquals(msg1.hashCode(), msg2.hashCode());
  }

  @Test()
  public void testEqualsDifferentStartLine() {
    RawMessage msg1 = RawMessage.create("testing");
    RawMessage msg2 = RawMessage.create("other");
    assertNotEquals(msg1, msg2);
    assertNotEquals(msg1.hashCode(), msg2.hashCode());
  }

  @Test()
  public void testEqualsDifferentBody() {
    RawMessage msg1 = RawMessage.create("testing");
    RawMessage msg2 = RawMessage.create("testing");
    msg1.setBody("testing".getBytes());
    msg2.setBody("other".getBytes());
    assertNotEquals(msg1, msg2);
    assertNotEquals(msg1.hashCode(), msg2.hashCode());
  }

}
