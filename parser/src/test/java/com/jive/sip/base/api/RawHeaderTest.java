package com.jive.sip.base.api;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

public class RawHeaderTest {

  @Test
  public void testEquals() {
    assertEquals(new RawHeader("name", "value"), new RawHeader("name", "value"));
  }

}
