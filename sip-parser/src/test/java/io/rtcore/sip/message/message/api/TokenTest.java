package io.rtcore.sip.message.message.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.base.api.Token;

public class TokenTest {

  @Test
  public void test() {
    assertEquals(Token.from("UDP"), Token.from("UDP"));
    assertEquals(Token.from("UDP"), Token.from("UdP"));
    assertNotEquals(Token.from("XXX"), Token.from("YYY"));
  }

  @Test
  public void testNullToken() {
    assertThrows(NullPointerException.class, () -> Token.from(null));
  }

  @Test
  public void testEmptyString() {
    assertThrows(IllegalArgumentException.class, () -> Token.from(""));
  }
}
