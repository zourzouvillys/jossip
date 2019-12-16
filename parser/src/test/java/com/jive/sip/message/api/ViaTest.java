/**
 *
 */
package com.jive.sip.message.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;
import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.QuotedString;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.impl.TokenParameterDefinition;

/**
 * @author Jeff Hutchins {@code <jhutchins@getjive.com>}
 *
 */
public class ViaTest {

  private HostAndPort host;
  private Token branch;

  @BeforeEach
  public void setup() {
    this.host = HostAndPort.fromString("localhost");
    this.branch = Token.from(Via.createBranch());
  }

  @Test
  public void test2ParamConstructor() {
    final Via via = new Via(ViaProtocol.TCP, this.host);
    assertTrue(via.getParameter(new TokenParameterDefinition(Via.BRANCH)).isPresent());
  }

  @Test
  public void testConstructorWithEmptyParamters() {
    final Via via = new Via(ViaProtocol.TCP, this.host, DefaultParameters.EMPTY);
    assertTrue(via.getParameter(new TokenParameterDefinition(Via.BRANCH)).isPresent());
  }

  @Test
  public void testConstructorWithParamtersNoBranch() {
    final Via via = new Via(ViaProtocol.TCP, this.host, DefaultParameters.EMPTY.withParameter(Token.from("foo"), QuotedString.from("bar")));
    assertTrue(via.getParameter(new TokenParameterDefinition(Via.BRANCH)).isPresent());
  }

  @Test
  public void testConstructorWithParamtersWithBranch() {
    final Via via = new Via(ViaProtocol.TCP, this.host, DefaultParameters.EMPTY.withParameter(Via.BRANCH, this.branch));
    assertEquals(this.branch, via.getParameter(new TokenParameterDefinition(Via.BRANCH)).get());
  }

}
