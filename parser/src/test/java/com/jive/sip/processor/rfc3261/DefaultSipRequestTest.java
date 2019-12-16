package com.jive.sip.processor.rfc3261;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.RequestBuilder;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.headers.CallId;
import com.jive.sip.uri.api.SipUri;

public class DefaultSipRequestTest {

  RequestBuilder reqBuilder;

  @BeforeEach
  public void setup() throws Exception {
    SipUri uri = SipUri.create(HostAndPort.fromParts("localhost", 5060));
    reqBuilder =
      new DefaultRequestBuilder()
        .setMethod(SipMethod.NOTIFY)
        .setRequestUri(uri)
        .setFrom(new NameAddr(uri))
        .setTo(new NameAddr(uri))
        .setCallID(new CallId("abcd"));
  }

  @Test
  public void testWithReplacedHeaders() throws Exception {
    SipRequest req = reqBuilder.setBody("12345").setHeader("l", 5).build();
    req = (SipRequest) req.withReplacedHeaders(new RawHeader("Content-Length", "9"));
    assert (req.getHeader("l").isPresent());
    assertEquals("5", req.getHeader("l").get().getValue());
    assert (req.getHeader("Content-Length").isPresent());
    assertEquals("9", req.getHeader("Content-Length").get().getValue());
  }

  @Test
  public void testWithReplacedHeader() throws Exception {
    SipRequest req = reqBuilder.setBody("12345").setHeader("l", 5).build();
    req = req.withBody("123456789");
    assert (!req.getHeader("l").isPresent());
    assert (req.getHeader("Content-Length").isPresent());
    assertEquals("9", req.getHeader("Content-Length").get().getValue());
  }

  @Test
  public void testWithoutHeaders() throws Exception {
    SipRequest req = reqBuilder.setHeader("l", 5).build();
    req = req.withoutHeaders("Content-Length");
    assert (req.getHeader("l").isPresent());
    req = req.withoutHeaders(DefaultSipMessage.CONTENT_LENGTH);
    assert (!req.getHeader("l").isPresent());
    assert (!req.getHeader("Content-Length").isPresent());
  }
}
