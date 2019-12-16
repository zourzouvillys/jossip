package com.jive.sip.processor.rfc3261;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.jive.sip.message.SipRequest;
import com.jive.sip.message.api.Replaces;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.headers.CallId;
import com.jive.sip.parsers.core.ParseFailureException;
import com.jive.sip.processor.uri.parsers.SipUriParser;
import com.jive.sip.uri.SipUri;

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
    final Replaces rep = req.getReplaces().get();
    assertEquals(SipMethod.REGISTER, req.getMethod());
    assertEquals(new CallId("xxx"), rep.getCallId());
    assertEquals("yyy", rep.getToTag());
    assertEquals("zzz", rep.getFromTag());
  }

}
