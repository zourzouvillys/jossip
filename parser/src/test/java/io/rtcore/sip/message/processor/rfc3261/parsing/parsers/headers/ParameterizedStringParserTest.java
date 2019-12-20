package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.message.api.headers.ParameterizedString;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.ParameterizedStringParser;

public class ParameterizedStringParserTest extends BaseParserTest<ParameterizedString> {

  public ParameterizedStringParserTest() {
    super(new ParameterizedStringParser());
  }

  @Test
  public void testBasic() {
    final ParameterizedString value = this.parse("en");
    assertEquals("en", value.value());
  }

  @Test
  public void testSimple() {
    final ParameterizedString value = this.parse("en-US");
    assertEquals("en-US", value.value());
  }

  @Test
  public void testWeightedWildcard() {
    final ParameterizedString value = this.parse("*;q=0.1");
    assertEquals("*", value.value());
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
