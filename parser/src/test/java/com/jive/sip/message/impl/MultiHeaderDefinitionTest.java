package com.jive.sip.message.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.base.api.Token;
import com.jive.sip.message.api.SipHeaderDefinition;
import com.jive.sip.message.api.Via;
import com.jive.sip.message.api.ViaProtocol;
import com.jive.sip.parameters.api.ParameterValue;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parsers.core.ParseFailureException;
import com.jive.sip.processor.rfc3261.MultiHeaderDefinition;
import com.jive.sip.processor.rfc3261.parsing.parsers.headers.ViaParser;

public class MultiHeaderDefinitionTest
{
  private SipHeaderDefinition<List<Via>> via;
  private List<RawHeader> headers;

  @Before
  public void setup()
  {
    this.via = MultiHeaderDefinition.create(new ViaParser(), "Via", 'v');
    this.headers = Lists.newLinkedList();
  }

  @Test
  public void testBasicVia()
  {
    headers.add(new RawHeader("Via", "SIP/2.0/UDP erlang.bell-telephone.com:5060;branch=z9hG4bK87asdks7"));
    headers.add(new RawHeader("v", "SIP/2.0/UDP 192.0.2.1:5060 ;received=192.0.2.207\n ;branch=z9hG4bK77asjd"));
    assertEquals(Lists.newArrayList(
        new Via(ViaProtocol.UDP,
            HostAndPort.fromString("erlang.bell-telephone.com:5060"),
            DefaultParameters.from(Lists.newArrayList(new RawParameter("branch", new TokenParameterValue("z9hG4bK87asdks7"))))),
        new Via(ViaProtocol.UDP,
            HostAndPort.fromString("192.0.2.1:5060"),
            DefaultParameters.from(Lists.newArrayList(new RawParameter("received", new TokenParameterValue("192.0.2.207")), new RawParameter("branch", new TokenParameterValue("z9hG4bK77asjd")))))),
        this.via.parse(this.headers));
  }

  @Test
  public void testAssineSpacing()
  {
    this.headers.add(new RawHeader("Via",
        "SIP / 2.0 / UDP first.example.com: 4000;ttl=16\r\n ;maddr=224.2.0.1"));// ;branch=z9hG4bKa7c6a8dlze.1"));
    Via actual = this.via.parse(this.headers).get(0);
    Via expected = new Via(ViaProtocol.UDP, HostAndPort.fromString("first.example.com:4000"));
    expected = expected.withParameters(DefaultParameters.EMPTY.withParameter(Token.from("ttl"), Token.from(16))
                                                              .withParameter(Token.from("maddr"), Token.from("224.2.0.1")));
//                                                              .withParameter(Token.from("branch"), Token.from("z9hG4bKa7c6a8dlze.1")));

    assertEquals(expected.getProtocol(), actual.getProtocol());
    assertEquals(expected.getSentBy(), actual.getSentBy());
    List<RawParameter> expectedParams = Lists.newArrayList(expected.getParameters().get().getRawParameters());
    List<RawParameter> actualParams = Lists.newArrayList(actual.getParameters().get().getRawParameters());
    ParameterValue<?> e = expectedParams.get(1).getValue();
    ParameterValue<?> a = actualParams.get(1).getValue();
    assertEquals(e, a);
    
//    assertEquals(Lists.newArrayList(
//        new Via(ViaProtocol.UDP,
//            HostAndPort.fromString("first.example.com:4000"))), 
//            DefaultParameters.from(Lists.newArrayList(new RawParameter("ttl", new TokenParameterValue("16")), 
//                                                      new RawParameter("maddr", new HostAndPortParameterValue("224.2.0.1")), 
//                                                      new RawParameter("branch", new TokenParameterValue("z9hG4bKa7c6a8dlze.1")))))),
//        this.via.parse(this.headers));
  }

  @Test
  public void testCollapsedHeader()
  {
    this.headers.add(new RawHeader("Via",
        "SIP / 2.0 / UDP first.example.com: 4000;ttl=16\r\n ;maddr=224.2.0.1 ;branch=z9hG4bKa7c6a8dlze.1, " +
            "SIP/2.0/UDP erlang.bell-telephone.com:5060;branch=z9hG4bK87asdks7"));
    this.headers.add(new RawHeader("v", "SIP/2.0/UDP 192.0.2.1:5060 ;received=192.0.2.207\n ;branch=z9hG4bK77asjd"));
    List<Via> vias = Lists.newLinkedList();
    
    Parameters params = DefaultParameters.EMPTY.withParameter(Token.from("ttl"), Token.from(16))
                                               .withParameter(Token.from("maddr"), Token.from("224.2.0.1"))
                                               .withParameter(Token.from("branch"), Token.from("z9hG4bKa7c6a8dlze.1"));
    vias.add(new Via(ViaProtocol.UDP, HostAndPort.fromString("first.example.com:4000"), params));
    
    params = DefaultParameters.EMPTY.withParameter(Token.from("branch"), Token.from("z9hG4bK87asdks7"));
    vias.add(new Via(ViaProtocol.UDP, HostAndPort.fromString("erlang.bell-telephone.com:5060"), params));
    
    params = DefaultParameters.EMPTY.withParameter(Token.from("received"), Token.from("192.0.2.207"))
                                    .withParameter(Token.from("branch"), Token.from("z9hG4bK77asjd"));
    vias.add(new Via(ViaProtocol.UDP, HostAndPort.fromString("192.0.2.1:5060"), params));
    
    List<Via> actual = this.via.parse(this.headers);
    
    assertEquals(3, actual.size());
    assertEquals(vias.get(0).getParameters().get(), actual.get(0).getParameters().get());
  }

  @Test(expected = ParseFailureException.class)
  public void testBadVia()
  {
    this.headers.add(new RawHeader("Via", "Bad Test"));
    this.via.parse(this.headers);
  }

}
