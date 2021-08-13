package io.rtcore.sip.message.processor.rfc3261;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;
import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.message.api.RequestBuilder;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.uri.SipUri;

public class DefaultRequestBuilderTest {

  private RequestBuilder builder;

  @BeforeEach
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

    assertEquals(new CSeq(1, SipMethod.OPTIONS), request.cseq());
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

    assertEquals(new CSeq(5, SipMethod.OPTIONS), request.cseq());
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

    assertEquals(new CSeq(7, SipMethod.OPTIONS), request.cseq());
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

    assertEquals(new CSeq(5, SipMethod.OPTIONS), request.cseq());
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

    assertEquals(new CSeq(5, SipMethod.OPTIONS), request.cseq());
  }

  @Test
  public void testSetCSeqByaddHeaderError() throws Exception {
    Assertions.assertThrows(ClassCastException.class, () -> this.builder.setHeader("CSeq", 5));
  }

  @Test
  public void testDefaultMaxForwards() throws Exception {
    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    final SipRequest request = this.builder.build();

    assertEquals(UnsignedInteger.valueOf(70), request.maxForwards().get());
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

    assertEquals(UnsignedInteger.valueOf(53), request.maxForwards().get());
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

    assertEquals(UnsignedInteger.valueOf(43), request.maxForwards().get());
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

    assertEquals(UnsignedInteger.valueOf(43), request.maxForwards().get());
  }

  @Test
  public void testSetMaxForwardsBySetHeaderError() throws Exception {
    assertThrows(ClassCastException.class, () -> this.builder.setHeader("max-ForWaRdS", "43"));
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

    final Collection<Via> vias = request.vias();
    assertEquals(vias.size(), 1);
    for (final Via via : vias) {
      assertEquals(expected, via);
    }
  }

  @Test
  public void testSetViaByHeader1() throws Exception {
    final String ip = "jeff.jive.com";
    final Via expected = new Via(ViaProtocol.UDP, HostAndPort.fromHost(ip));

    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts(ip, 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("Via", expected);
    final SipRequest request = this.builder.build();

    final Collection<Via> vias = request.vias();
    assertEquals(vias.size(), 1);
    for (final Via via : vias) {
      assertEquals(expected, via);
    }
  }

  @Test
  public void testSetViaByHeader2() throws Exception {
    final String ip = "jeff.jive.com";
    final Via expected = new Via(ViaProtocol.UDP, HostAndPort.fromHost(ip));

    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts(ip, 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("VIA", expected);
    final SipRequest request = this.builder.build();

    final Collection<Via> vias = request.vias();
    assertEquals(vias.size(), 1);
    for (final Via via : vias) {
      assertEquals(expected, via);
    }
  }

  @Test
  public void testSetViaByHeader3() throws Exception {

    final String ip = "jeff.jive.com";
    final Via expected = new Via(ViaProtocol.UDP, HostAndPort.fromHost(ip));

    this.builder.setMethod(SipMethod.OPTIONS);
    this.builder.setRequestUri(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060)));
    this.builder.setFrom(new NameAddr(new SipUri(HostAndPort.fromParts(ip, 5060))));
    this.builder.setTo(new NameAddr(new SipUri(HostAndPort.fromParts("127.0.0.1", 5060))));
    this.builder.setCallID(new CallId("jhutchins"));
    this.builder.setHeader("v", expected);
    final SipRequest request = this.builder.build();

    final Collection<Via> vias = request.vias();
    assertEquals(vias.size(), 1);
    for (final Via via : vias) {
      assertEquals(expected, via);
    }
  }

  @Test
  public void testSetViaByHeaderError() throws Exception {
    assertThrows(ClassCastException.class, () -> this.builder.setHeader("Via", "test.example.com"));
  }
}
