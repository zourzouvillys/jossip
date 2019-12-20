package io.rtcore.sip.message.parsers.core.terminal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.parsers.core.terminal.LinearWhitespaceParser;

public class LinearWhitespaceParserTest extends BaseParserTest<CharSequence> {

  public LinearWhitespaceParserTest() {
    super(new LinearWhitespaceParser());
  }

  @Test
  public void test() {
    assertEquals(" ", this.parse(" "));
    assertEquals("     ", this.parse("     "));
    assertEquals(" \t   ", this.parse(" \t   "));
    assertEquals(" \n ", this.parse(" \n "));
    assertEquals(" \r\n ", this.parse(" \r\n "));
    assertEquals(" ", this.parse(" "));
    assertEquals(" ", this.parse(" \n", 1));
    assertEquals("\t", this.parse("\t\n", 1));
  }

}
