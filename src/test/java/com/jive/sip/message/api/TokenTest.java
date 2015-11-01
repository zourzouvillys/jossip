package com.jive.sip.message.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import com.jive.sip.base.api.Token;

public class TokenTest
{

  @Test
  public void test()
  {
    assertEquals(Token.from("UDP"), Token.from("UDP"));
    assertEquals(Token.from("UDP"), Token.from("UdP"));
    assertNotEquals(Token.from("XXX"), Token.from("YYY"));
  }

  @Test(expected = NullPointerException.class)
  public void testNullToken()
  {
    Token.from(null);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void testEmptyString()
  {
    Token.from("");
  }
}
