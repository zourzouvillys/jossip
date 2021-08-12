package io.rtcore.sip.message.message.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.SipHeaderDefinition;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.parameters.api.ParameterValue;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parsers.core.ParseFailureException;
import io.rtcore.sip.message.processor.rfc3261.MultiHeaderDefinition;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ViaParser;

public class MultiHeaderDefinitionTest {
  private SipHeaderDefinition<List<Via>> via;
  private List<RawHeader> headers;

  @BeforeEach
  public void setup() {
    this.via = MultiHeaderDefinition.create(new ViaParser(), "Via", 'v');
    this.headers = Lists.newLinkedList();
  }

  @Test
  public void testBasicVia() {
    headers.add(new RawHeader("Via", "SIP/2.0/UDP erlang.bell-telephone.com:5060;branch=z9hG4bK87asdks7"));
    headers.add(new RawHeader("v", "SIP/2.0/UDP 192.0.2.1:5060 ;received=192.0.2.207\n ;branch=z9hG4bK77asjd"));
    assertEquals(Lists.newArrayList(
      new Via(
        ViaProtocol.UDP,
        HostAndPort.fromString("erlang.bell-telephone.com:5060"),
        DefaultParameters.from(Lists.newArrayList(new RawParameter("branch", new TokenParameterValue("z9hG4bK87asdks7"))))),
      new Via(
        ViaProtocol.UDP,
        HostAndPort.fromString("192.0.2.1:5060"),
        DefaultParameters.from(Lists.newArrayList(new RawParameter("received", new TokenParameterValue("192.0.2.207")),
          new RawParameter("branch", new TokenParameterValue("z9hG4bK77asjd")))))),
      this.via.parse(this.headers));
  }

  @Test
  public void testAssineSpacing() {
    this.headers.add(new RawHeader(
      "Via",
      "SIP / 2.0 / UDP first.example.com: 4000;ttl=16\r\n ;maddr=224.2.0.1"));// ;branch=z9hG4bKa7c6a8dlze.1"));
    Via actual = this.via.parse(this.headers).get(0);
    Via expected = new Via(ViaProtocol.UDP, HostAndPort.fromString("first.example.com:4000"));
    expected =
      expected.withParameters(DefaultParameters.EMPTY.withParameter(Token.from("ttl"), Token.from(16))
        .withParameter(Token.from("maddr"), Token.from("224.2.0.1")));
    // .withParameter(Token.from("branch"), Token.from("z9hG4bKa7c6a8dlze.1")));

    assertEquals(expected.protocol(), actual.protocol());
    assertEquals(expected.sentBy(), actual.sentBy());
    List<RawParameter> expectedParams = Lists.newArrayList(expected.getParameters().get().getRawParameters());
    List<RawParameter> actualParams = Lists.newArrayList(actual.getParameters().get().getRawParameters());
    ParameterValue<?> e = expectedParams.get(1).value();
    ParameterValue<?> a = actualParams.get(1).value();
    assertEquals(e, a);

    // assertEquals(Lists.newArrayList(
    // new Via(ViaProtocol.UDP,
    // HostAndPort.fromString("first.example.com:4000"))),
    // DefaultParameters.from(Lists.newArrayList(new RawParameter("ttl", new
    // TokenParameterValue("16")),
    // new RawParameter("maddr", new HostAndPortParameterValue("224.2.0.1")),
    // new RawParameter("branch", new TokenParameterValue("z9hG4bKa7c6a8dlze.1")))))),
    // this.via.parse(this.headers));
  }

  @Test
  public void testCollapsedHeader() {
    this.headers.add(new RawHeader(
      "Via",
      "SIP / 2.0 / UDP first.example.com: 4000;ttl=16\r\n ;maddr=224.2.0.1 ;branch=z9hG4bKa7c6a8dlze.1, "
        +
        "SIP/2.0/UDP erlang.bell-telephone.com:5060;branch=z9hG4bK87asdks7"));
    this.headers.add(new RawHeader("v", "SIP/2.0/UDP 192.0.2.1:5060 ;received=192.0.2.207\n ;branch=z9hG4bK77asjd"));
    List<Via> vias = Lists.newLinkedList();

    Parameters params =
      DefaultParameters.EMPTY.withParameter(Token.from("ttl"), Token.from(16))
        .withParameter(Token.from("maddr"), Token.from("224.2.0.1"))
        .withParameter(Token.from("branch"), Token.from("z9hG4bKa7c6a8dlze.1"));
    vias.add(new Via(ViaProtocol.UDP, HostAndPort.fromString("first.example.com:4000"), params));

    params = DefaultParameters.EMPTY.withParameter(Token.from("branch"), Token.from("z9hG4bK87asdks7"));
    vias.add(new Via(ViaProtocol.UDP, HostAndPort.fromString("erlang.bell-telephone.com:5060"), params));

    params =
      DefaultParameters.EMPTY.withParameter(Token.from("received"), Token.from("192.0.2.207"))
        .withParameter(Token.from("branch"), Token.from("z9hG4bK77asjd"));
    vias.add(new Via(ViaProtocol.UDP, HostAndPort.fromString("192.0.2.1:5060"), params));

    List<Via> actual = this.via.parse(this.headers);

    assertEquals(3, actual.size());
    assertEquals(vias.get(0).getParameters().get(), actual.get(0).getParameters().get());
  }

  @Test
  public void testBadVia() {
    this.headers.add(new RawHeader("Via", "Bad Test"));
    assertThrows(ParseFailureException.class, () -> this.via.parse(this.headers));
  }

}
