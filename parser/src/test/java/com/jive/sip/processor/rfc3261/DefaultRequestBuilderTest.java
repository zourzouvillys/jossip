package com.jive.sip.processor.rfc3261;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import com.google.common.net.HostAndPort;
import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.message.api.CSeq;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.RequestBuilder;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.Via;
import com.jive.sip.message.api.ViaProtocol;
import com.jive.sip.message.api.headers.CallId;
import com.jive.sip.uri.api.SipUri;

public class DefaultRequestBuilderTest {

  private RequestBuilder builder;

  @Before
  public void setup() {
    this.builder = new DefaultRequestBuilder();
  }

  @Test
  public void testDefaultCSeq() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    final SipRequest request = this.builder.build();

    assertEquals(new CSeq(1, SipMethod.OPTIONS), request.getCSeq());
  }

  @Test
  public void testSetCSeq() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setCSeq(new CSeq(5, SipMethod.OPTIONS));
    final SipRequest request = this.builder.build();

    assertEquals(new CSeq(5, SipMethod.OPTIONS), request.getCSeq());
  }

  @Test
  public void testCSeqManagement() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setCSeq(new CSeq(5, SipMethod.OPTIONS));
    SipRequest request = this.builder.build();
    request = this.builder.build();
    request = this.builder.build();

    assertEquals(new CSeq(7, SipMethod.OPTIONS), request.getCSeq());
  }

  @Test
  public void testSetCSeqByaddHeader1() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("CSeq", (new CSeq(5, SipMethod.OPTIONS)));
    final SipRequest request = this.builder.build();

    assertEquals(new CSeq(5, SipMethod.OPTIONS), request.getCSeq());
  }

  @Test
  public void testSetCSeqByaddHeader2() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("cseq", (new CSeq(5, SipMethod.OPTIONS)));
    final SipRequest request = this.builder.build();

    assertEquals(new CSeq(5, SipMethod.OPTIONS), request.getCSeq());
  }

  @Test(expected = ClassCastException.class)
  public void testSetCSeqByaddHeaderError() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("CSeq", 5);
    final SipRequest request = this.builder.build();

    assertEquals(new CSeq(5, SipMethod.OPTIONS), request.getCSeq());
  }

  @Test
  public void testDefaultMaxForwards() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    final SipRequest request = this.builder.build();

    assertEquals(UnsignedInteger.valueOf(70), request.getMaxForwards().get());
  }

  @Test
  public void testSetMaxForwards() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setMaxForwards(53);
    final SipRequest request = this.builder.build();

    assertEquals(UnsignedInteger.valueOf(53), request.getMaxForwards().get());
  }

  @Test
  public void testSetMaxForwardsBySetHeader1() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("Max-Forwards", 43);
    final SipRequest request = this.builder.build();

    assertEquals(UnsignedInteger.valueOf(43), request.getMaxForwards().get());
  }

  @Test
  public void testSetMaxForwardsBySetHeader2() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("max-ForWaRdS", 43);
    final SipRequest request = this.builder.build();

    assertEquals(UnsignedInteger.valueOf(43), request.getMaxForwards().get());
  }

  @Test(expected = ClassCastException.class)
  public void testSetMaxForwardsBySetHeaderError() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("max-ForWaRdS", "43");
    final SipRequest request = this.builder.build();

    assertEquals(UnsignedInteger.valueOf(43), request.getMaxForwards().get());
  }

  @Test
  public void testSetVia() throws Exception {
    final String ip = "jeff.jive.com";
    final Via expected = new Via(ViaProtocol.UDP, HostAndPort.fromParts(ip, 5060));

    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts(ip, 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setVia(expected);
    final SipRequest request = this.builder.build();

    final Collection<Via> vias = request.getVias();
    assertEquals(vias.size(), 1);
    for (final Via via : vias) {
      assertEquals(via, expected);
    }
  }

  @Test
  public void testSetViaByHeader1() throws Exception {
    final String ip = "jeff.jive.com";
    final Via expected = new Via(ViaProtocol.UDP, HostAndPort.fromParts(ip, 5060));

    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts(ip, 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("Via", expected);
    final SipRequest request = this.builder.build();

    final Collection<Via> vias = request.getVias();
    assertEquals(vias.size(), 1);
    for (final Via via : vias) {
      assertEquals(via, expected);
    }
  }

  @Test
  public void testSetViaByHeader2() throws Exception {
    final String ip = "jeff.jive.com";
    final Via expected = new Via(ViaProtocol.UDP, HostAndPort.fromParts(ip, 5060));

    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts(ip, 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("VIA", expected);
    final SipRequest request = this.builder.build();

    final Collection<Via> vias = request.getVias();
    assertEquals(vias.size(), 1);
    for (final Via via : vias) {
      assertEquals(via, expected);
    }
  }

  @Test
  public void testSetViaByHeader3() throws Exception {
    final String ip = "jeff.jive.com";
    final Via expected = new Via(ViaProtocol.UDP, HostAndPort.fromParts(ip, 5060));

    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts(ip, 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("v", expected);
    final SipRequest request = this.builder.build();

    final Collection<Via> vias = request.getVias();
    assertEquals(vias.size(), 1);
    for (final Via via : vias) {
      assertEquals(via, expected);
    }
  }

  @Test(expected = ClassCastException.class)
  public void testSetViaByHeaderError() throws Exception {
    final String ip = "jeff.jive.com";
    final Via expected = new Via(ViaProtocol.UDP, HostAndPort.fromParts(ip, 5060));

    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts(ip, 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("Via", ip);
    final SipRequest request = this.builder.build();

    final Collection<Via> vias = request.getVias();
    assertEquals(vias.size(), 1);
    for (final Via via : vias) {
      assertEquals(via, expected);
    }
  }
}
