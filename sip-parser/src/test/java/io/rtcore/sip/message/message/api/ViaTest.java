/**
 *
 */
package io.rtcore.sip.message.message.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.QuotedString;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;

/**
 * 
 *
 */
public class ViaTest {

  private HostAndPort host;
  private Token branch;

  @BeforeEach
  public void setup() {
    this.host = HostAndPort.fromString("localhost");
    this.branch = Token.from(UUID.randomUUID().toString());
  }

  @Test
  public void test2ParamConstructor() {
    final Via via = new Via(ViaProtocol.TCP, this.host);
    assertFalse(via.getParameter(new TokenParameterDefinition(Via.BRANCH)).isPresent());
  }

  @Test
  public void testConstructorWithEmptyParamters() {
    final Via via = new Via(ViaProtocol.TCP, this.host, DefaultParameters.EMPTY);
    assertFalse(via.getParameter(new TokenParameterDefinition(Via.BRANCH)).isPresent());
  }

  @Test
  public void testConstructorWithParamtersNoBranch() {

    final Via via =
      new Via(
        ViaProtocol.TCP,
        this.host,
        DefaultParameters.EMPTY.withParameter(Token.from("foo"), QuotedString.from("bar")));

    assertFalse(via.getParameter(new TokenParameterDefinition(Via.BRANCH)).isPresent());

  }

  @Test
  public void testConstructorWithParamtersWithBranch() {
    final Via via = new Via(ViaProtocol.TCP, this.host, DefaultParameters.EMPTY.withParameter(Via.BRANCH, this.branch));
    assertEquals(this.branch, via.getParameter(new TokenParameterDefinition(Via.BRANCH)).get());
  }

}
