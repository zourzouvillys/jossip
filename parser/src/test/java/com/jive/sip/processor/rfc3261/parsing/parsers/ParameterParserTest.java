package com.jive.sip.processor.rfc3261.parsing.parsers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.impl.FlagParameterDefinition;
import com.jive.sip.parameters.impl.TokenParameterDefinition;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.parsers.core.ParameterParser;

public class ParameterParserTest extends BaseParserTest<Collection<RawParameter>> {

  public ParameterParserTest() {
    super(ParameterParser.getInstance());
  }

  @Test
  public void test() {
    this.parse(";moo;cows=2");
    this.parse(" ; moo;cows=2");
    this.parse(";    moo;  cows ; x-meep ; +moo");
    Assert.assertEquals(1, this.parse(";cows=2").size());

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
