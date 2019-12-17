package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.jive.sip.message.api.headers.ParameterizedString;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.processor.rfc3261.parsing.parsers.ParameterizedStringParser;

public class ParameterizedStringParserTest extends BaseParserTest<ParameterizedString> {

  public ParameterizedStringParserTest() {
    super(new ParameterizedStringParser());
  }

  @Test
  public void testSimple() {
    final ParameterizedString value = this.parse("en");
    assertEquals("en", value.value());
  }

  @Test
  public void testWithParam() {
    final ParameterizedString value = this.parse("en;q=0.1");
    assertEquals("en", value.value());
    assertEquals("0.1", value.getParameters().get().getParameter("q").get());
  }

  @Test
  public void testWeirdSpacing() {
    final ParameterizedString value = this.parse("en  ;  q=0.1");
    assertEquals("en", value.value());
    assertEquals("0.1", value.getParameters().get().getParameter("q").get());
  }

}
