package com.jive.sip.processor.rfc3261;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Lists;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.Replaces;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.headers.CallId;
import com.jive.sip.parsers.core.ParseFailureException;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManager;
import com.jive.sip.processor.rfc3261.serializing.RfcSerializerManagerBuilder;
import com.jive.sip.processor.uri.parsers.SipUriParser;
import com.jive.sip.uri.api.SipUri;

public class RfcSipMessageManagerTest
{
  private final RfcSipMessageManager manager = new RfcSipMessageManager();
  private final RfcSerializerManager serializer = new RfcSerializerManagerBuilder().build();

  @Test
  public void testEscapedUri()
  {
    this.manager.createRequest("SUBSCRIBE xxx:%20 SIP/2.0", Lists.<RawHeader> newArrayList());
  }

  @Test
  public void test()
  {
    this.manager.createRequest("INVITE sip:theo SIP/2.0", Lists.<RawHeader> newArrayList());
  }

  @Test(expected = ParseFailureException.class)
  public void testMultipleSpaces()
  {
    this.manager.createRequest("INVITE   sip:theo SIP/2.0", Lists.<RawHeader> newArrayList());
  }

  @Test(expected = ParseFailureException.class)
  public void testMissingVersion()
  {
    this.manager.createRequest("INVITE sip:theo", Lists.<RawHeader> newArrayList());
  }

  @Test
  public void testFromEmbedded()
  {
    final SipUri uri = SipUriParser.parse("sip:theo@test.com;method=REGISTER?Replaces=xxx%3Bto-tag%3Dyyy%3Bfrom-tag%3Dzzz");
    final SipRequest req = this.manager.fromUri(uri);
    final Replaces rep = req.getReplaces().get();
    assertEquals(SipMethod.REGISTER, req.getMethod());
    assertEquals(new CallId("xxx"), rep.getCallId());
    assertEquals("yyy", rep.getToTag());
    assertEquals("zzz", rep.getFromTag());
  }

}
