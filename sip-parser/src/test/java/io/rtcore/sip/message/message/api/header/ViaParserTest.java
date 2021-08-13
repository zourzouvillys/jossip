/**
 *
 */
package io.rtcore.sip.message.message.api.header;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.parameters.impl.FlagParameterDefinition;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;
import io.rtcore.sip.message.parameters.tools.ParameterUtils;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.ViaParser;

/**
 *
 *
 */
public class ViaParserTest extends BaseParserTest<Via> {

  public ViaParserTest() {
    super(new ViaParser());
  }

  /**
   * Test method for
   * {@link io.rtcore.sip.message.processor.rfc3261.headers.Via#Via(java.lang.String, java.lang.String)}.
   *
   * @throws SipMessageParseFailureException
   */
  @Test
  public void testViaHeader() throws SipMessageParseFailureException {
    final String test = "SIP / 2.0 / UdP first.example.com: 4000;ttl=16;maddr=224.2.0.1 ;branch=z9hG4bKa7c6a8dlze.1";
    final Via header = this.parse(test);
    assertEquals(ViaProtocol.of("SIP", "2.0", "UdP"), header.protocol());
    assertEquals("first.example.com:4000", header.sentBy().toUriString());
    assertEquals(Token.from("16"), new TokenParameterDefinition("ttl").parse(header.getParameters().get()).orElse(null));
    assertEquals(Token.from("224.2.0.1"), new TokenParameterDefinition("maddr").parse(header.getParameters().get()).orElse(null));
    assertEquals(Token.from("z9hG4bKa7c6a8dlze.1"), ParameterUtils.Branch.parse(header.getParameters().get()).orElse(null));
  }

  @Test
  public void testViaHeaderWithValuelessParam() throws SipMessageParseFailureException {

    final String test = "SIP/2.0/XXX 172.20.103.51;branch=z9hG4bK-ac271308;rport";

    final Via header = this.parse(test);

    assertEquals("SIP", header.protocol().name());
    assertEquals("2.0", header.protocol().version());
    assertEquals("XXX", header.protocol().transport());

    assertEquals("172.20.103.51", header.sentBy().toUriString());
    assertEquals(Token.from("z9hG4bK-ac271308"), ParameterUtils.Branch.parse(header.getParameters().get()).orElse(null));
    assertTrue(new FlagParameterDefinition("rport").parse(header.getParameters().get()).isPresent());

  }

  @Test
  public void testParseWithIpAndPort() {
    this.parse("SIP/2.0/UDP 199.36.250.60:5061;branch=0");
  }

}
