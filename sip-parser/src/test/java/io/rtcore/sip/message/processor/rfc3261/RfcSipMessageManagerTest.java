package io.rtcore.sip.message.processor.rfc3261;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.api.Replaces;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.parsers.core.ParseFailureException;
import io.rtcore.sip.message.processor.uri.parsers.SipUriParser;
import io.rtcore.sip.message.uri.SipUri;

public class RfcSipMessageManagerTest {
  private final RfcSipMessageManager manager = new RfcSipMessageManager();

  @Test
  public void testEscapedUri() {
    this.manager.createRequest("SUBSCRIBE xxx:%20 SIP/2.0", newArrayList());
  }

  @Test
  public void test() {
    this.manager.createRequest("INVITE sip:theo SIP/2.0", newArrayList());
  }

  @Test
  public void testMultipleSpaces() {
    assertThrows(ParseFailureException.class, () -> this.manager.createRequest("INVITE   sip:theo SIP/2.0", newArrayList()));
  }

  @Test
  public void testMissingVersion() {
    assertThrows(ParseFailureException.class, () -> this.manager.createRequest("INVITE sip:theo", newArrayList()));
  }

  @Test
  public void testFromEmbedded() {
    final SipUri uri = SipUriParser.parse("sip:theo@test.com;method=REGISTER?Replaces=xxx%3Bto-tag%3Dyyy%3Bfrom-tag%3Dzzz");
    final SipRequest req = this.manager.fromUri(uri, SipMethod.INVITE);
    final Replaces rep = req.replaces().get();
    assertEquals(SipMethod.REGISTER, req.method());
    assertEquals(new CallId("xxx"), rep.callId());
    assertEquals("yyy", rep.getToTag());
    assertEquals("zzz", rep.getFromTag());
  }

}
