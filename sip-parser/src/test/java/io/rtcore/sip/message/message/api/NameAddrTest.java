/**
 *
 */
package io.rtcore.sip.message.message.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;
import io.rtcore.sip.message.uri.SipUri;

/**
 * 
 *
 */
public class NameAddrTest {

  @Test
  public void parameterTest() {
    final NameAddr name =
      new NameAddr(new SipUri(HostAndPort.fromString("10.199.3.1:5061")))
        .withParameter(Token.from("ftag"), Token.from("a29dd1ac97e3b91e"))
        .withParameter(Token.from("lr"), Token.from("on"));

    assertTrue(name.getParameters().isPresent());

    assertEquals(Token.from("a29dd1ac97e3b91e"), name.getParameter(new TokenParameterDefinition("ftag")).get());

    assertEquals(Token.from("on"), name.getParameter(new TokenParameterDefinition("lr")).get());

  }

  @Test
  public void testWithUri() {
    final NameAddr name = new NameAddr(new SipUri(HostAndPort.fromString("10.199.3.1:5061"))).withParameter(Token.from("expires"), Token.from("0"));
    assertEquals(0, name.getExpires().orElseThrow());
  }

}
