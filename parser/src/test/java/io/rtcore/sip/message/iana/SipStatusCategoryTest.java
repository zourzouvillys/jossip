package io.rtcore.sip.message.iana;

import static io.rtcore.sip.message.iana.SipStatusCategory.*;
import static io.rtcore.sip.message.iana.SipStatusCategory.forCode;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SipStatusCategoryTest {

  @Test
  void test() {
    assertEquals(PROVISIONAL, forCode(100));
    assertEquals(PROVISIONAL, forCode(101));
    assertEquals(PROVISIONAL, forCode(199));
    assertEquals(SUCCESSFUL, forCode(200));
    assertEquals(SUCCESSFUL, forCode(299));
    assertEquals(REDIRECTION, forCode(300));
    assertEquals(REQUEST_FAILURE, forCode(400));
    assertEquals(SERVER_FAILURE, forCode(500));
    assertEquals(GLOBAL_FAILURE, forCode(600));
  }

  @Test
  void testInvalidThrows() {
    assertThrows(Exception.class, () -> forCode(99));
    assertThrows(Exception.class, () -> forCode(700));
    assertThrows(Exception.class, () -> forCode(0));
  }
}
