/**
 * 
 */
package io.rtcore.sip.message.base.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.base.api.RawMessage;

public class RawMessageTest {

  @Test
  public void testConstructorNull() {
    assertThrows(NullPointerException.class, () -> RawMessage.create(null));
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
