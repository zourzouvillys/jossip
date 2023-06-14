package io.rtcore.sip.iana;

import static io.rtcore.sip.common.iana.SipStatusCategory.GLOBAL_FAILURE;
import static io.rtcore.sip.common.iana.SipStatusCategory.TRYING;
import static io.rtcore.sip.common.iana.SipStatusCategory.PROVISIONAL;
import static io.rtcore.sip.common.iana.SipStatusCategory.REDIRECTION;
import static io.rtcore.sip.common.iana.SipStatusCategory.REQUEST_FAILURE;
import static io.rtcore.sip.common.iana.SipStatusCategory.SERVER_FAILURE;
import static io.rtcore.sip.common.iana.SipStatusCategory.SUCCESSFUL;
import static io.rtcore.sip.common.iana.SipStatusCategory.forCode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class SipStatusCategoryTest {

  @Test
  void test() {
    assertEquals(TRYING, forCode(100));
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
