package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.parsers.core.QuotedStringParser;

public class QuotedStringParserTest extends BaseParserTest<CharSequence> {

  public QuotedStringParserTest() {
    super(QuotedStringParser.INSTANCE);
  }

  @Test
  public void testEmptyString() {
    assertEquals("", this.parse("\"\""));
  }

  @Test
  public void testLeadingSWS() {
    assertEquals("", this.parse(" \"\""));
    assertEquals("", this.parse("  \"\""));
  }

  @Test
  public void testOneChar() {
    assertEquals("1", this.parse("\"1\""));
  }

  @Test
  public void testLWS() {
    assertEquals(" a ", this.parse("\" a \""));
  }

  @Test
  public void testEscape() {
    assertEquals("\\\"", this.parse("\"\\\"\""));
  }
}
