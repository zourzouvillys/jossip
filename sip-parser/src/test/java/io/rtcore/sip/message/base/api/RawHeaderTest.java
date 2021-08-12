package io.rtcore.sip.message.base.api;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.base.api.RawHeader;

public class RawHeaderTest {

  @Test
  public void testEquals() {
    assertEquals(new RawHeader("name", "value"), new RawHeader("name", "value"));
  }

}
