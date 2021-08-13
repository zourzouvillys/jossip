package io.rtcore.sip.message.processor.rfc3261.parsing.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.impl.FlagParameterDefinition;
import io.rtcore.sip.message.parameters.impl.TokenParameterDefinition;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.parsers.core.ParameterParser;

public class ParameterParserTest extends BaseParserTest<Collection<RawParameter>> {

  public ParameterParserTest() {
    super(ParameterParser.getInstance());
  }

  @Test
  public void test() {
    this.parse(";moo;cows=2");
    this.parse(" ; moo;cows=2");
    this.parse(";    moo;  cows ; x-meep ; +moo");
    assertEquals(1, this.parse(";cows=2").size());

  }

  @Test
  public void test2() {
    final Collection<RawParameter> params = this.parse(";cows");
    final Parameters p = DefaultParameters.from(params);
    assertFalse(new TokenParameterDefinition("x-cows").parse(p).isPresent());
    assertEquals(1, params.size());
    assertTrue(new FlagParameterDefinition("cows").parse(p).isPresent());
  }

  @Test
  public void test3() {
    final Collection<RawParameter> params = this.parse(";moo=cows;lr;meep=1;+xxx=2");
    final Parameters p = DefaultParameters.from(params);
    assertEquals(4, params.size());
    assertTrue(new TokenParameterDefinition("moo").parse(p).isPresent());
    assertEquals(Token.from("cows"), new TokenParameterDefinition("moo").parse(p).orElse(null));
  }

}
