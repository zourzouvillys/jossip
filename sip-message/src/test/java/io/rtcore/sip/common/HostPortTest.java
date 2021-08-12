package io.rtcore.sip.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HostPortTest {

  @Test
  void test() {
    assertEquals("invalid", HostPort.fromHost("invaliD").host().toUriString());
    assertEquals("test.com", HostPort.fromHost("test.COM").host().toUriString());
    assertEquals("127.0.0.1", HostPort.fromHost("127.0.0.1").host().toUriString());
    assertEquals("0.0.0.0", HostPort.fromHost("0.0.0.0").host().toUriString());
    assertEquals("[::1]", HostPort.fromHost("[0:0::1]").host().toUriString());
    assertEquals("[::1]", HostPort.fromHost("0:0::1").host().toUriString());
  }

}
