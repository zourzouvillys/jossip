package io.rtcore.sip.message.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.FlagParameterValue;
import io.rtcore.sip.message.parameters.api.HostAndPortParameterValue;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.QuotedStringParameterValue;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.impl.FlagParameterDefinition;
import io.rtcore.sip.message.parameters.impl.HostParameterDefinition;
import io.rtcore.sip.message.parameters.impl.QuotedStringParameterDefinition;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;

public class SipParameterDefinitionTest {
  private Parameters parameters =
    DefaultParameters.from(Lists.newArrayList(new RawParameter(Token.from("domain"), new HostAndPortParameterValue("jive.com:5060")),
      new RawParameter(Token.from("tag"), new TokenParameterValue("army_dave")),
      new RawParameter(Token.from("list"), new QuotedStringParameterValue("a,b,c")),
      new RawParameter(Token.from("lr"), new FlagParameterValue()),
      new RawParameter(Token.from("messedUpFlag"), new TokenParameterValue("true"))));

  @Test
  public void testFlagParameter() {
    Optional<Token> result = new FlagParameterDefinition("lr").parse(parameters);

    assertTrue(result.isPresent());
    assertEquals(Token.from("lr"), result.get());
  }

  @Test
  public void testFlagNotFound() {
    Optional<Token> result = new FlagParameterDefinition("notLr").parse(parameters);

    assertFalse(result.isPresent());
  }

  @Test
  public void testFlagWithValue() {
    Optional<Token> result = new FlagParameterDefinition("messedUpFlag").parse(parameters);

    assertTrue(result.isPresent());
    assertEquals(Token.from("messedUpFlag"), result.get());
  }

  @Test
  public void testHostParameter() {
    Optional<HostAndPort> result = new HostParameterDefinition("domain").parse(parameters);

    assertTrue(result.isPresent());
    assertEquals(HostAndPort.fromParts("jive.com", 5060), result.get());
  }

  @Test
  public void testHostNotFound() {
    Optional<HostAndPort> result = new HostParameterDefinition("notDomain").parse(parameters);

    assertFalse(result.isPresent());
  }

  @Test
  public void testTokenParameter() {
    Optional<Token> result = new TokenParameterDefinition("tag").parse(parameters);

    assertTrue(result.isPresent());
    assertEquals(Token.from("army_dave"), result.get());
  }

  @Test
  public void testTokenNotFound() {
    Optional<Token> result = new TokenParameterDefinition("notTag").parse(parameters);

    assertFalse(result.isPresent());
  }

  @Test
  public void testQuotedStringParameter() {
    Optional<String> result = new QuotedStringParameterDefinition("list").parse(parameters);

    assertTrue(result.isPresent());
    assertEquals("a,b,c", result.get());
  }

  @Test
  public void testQuotedStringNotFound() {
    Optional<String> result = new QuotedStringParameterDefinition("notList").parse(parameters);

    assertFalse(result.isPresent());
  }

  @Test
  public void testTokenOrQuotedParameter() {
    Optional<String> result = new QuotedStringParameterDefinition("list").parse(parameters);

    assertTrue(result.isPresent());
    assertEquals("a,b,c", result.get());
  }

  @Test
  public void testTokenOrQuotedNotFound() {
    Optional<String> result = new QuotedStringParameterDefinition("lr").parse(parameters);

    assertFalse(result.isPresent());
  }
}
