/**
 * 
 */
package io.rtcore.sip.message.processor.uri.parsers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.uri.TelUri;

/**
 * 
 * 
 */
public class TelUriParserTest extends BaseParserTest<TelUri> {

  public TelUriParserTest() {
    super(TelUriParser.TEL);
  }

  @Test
  public void test1() {
    assertEquals(new TelUri("+1-201-555-0123", DefaultParameters.from(Lists.<RawParameter>newArrayList())), this.parse("+1-201-555-0123"));
  }

  @Test
  public void test2() {
    assertEquals(new TelUri(
      "7042",
      DefaultParameters.from(Lists.newArrayList(new RawParameter("phone-context", new TokenParameterValue("example.com"))))),
      this.parse("7042;phone-context=example.com"));
  }

  @Test
  public void test3() {
    assertEquals(new TelUri("863-1234", DefaultParameters.from(Lists.newArrayList(new RawParameter("phone-context", new TokenParameterValue("+1-914-555"))))),
      this.parse("863-1234;phone-context=+1-914-555"));
  }

  @Test
  public void testStaticParsing() {
    assertEquals(
      new TelUri(
        "863-1234",
        DefaultParameters.from(Lists.newArrayList(new RawParameter(
          "phone-context",
          new TokenParameterValue("+1-914-555"))))),
      TelUriParser.parse("tel:863-1234;phone-context=+1-914-555"));
  }

}
