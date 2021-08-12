package io.rtcore.sip.message.processor.rfc3261;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.RequestBuilder;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.processor.rfc3261.DefaultRequestBuilder;
import io.rtcore.sip.message.processor.rfc3261.DefaultSipMessage;
import io.rtcore.sip.message.uri.SipUri;

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
    assertEquals("5", req.getHeader("l").get().value());
    assert (req.getHeader("Content-Length").isPresent());
    assertEquals("9", req.getHeader("Content-Length").get().value());
  }

  @Test
  public void testWithReplacedHeader() throws Exception {
    SipRequest req = reqBuilder.setBody("12345").setHeader("l", 5).build();
    req = req.withBody("123456789");
    assert (!req.getHeader("l").isPresent());
    assert (req.getHeader("Content-Length").isPresent());
    assertEquals("9", req.getHeader("Content-Length").get().value());
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
