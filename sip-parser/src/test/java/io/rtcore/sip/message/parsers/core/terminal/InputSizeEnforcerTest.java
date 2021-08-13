package io.rtcore.sip.message.parsers.core.terminal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Range;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.core.BaseParserTest;
import io.rtcore.sip.message.parsers.core.ParserUtils;

public class InputSizeEnforcerTest extends BaseParserTest<CharSequence> {

  public InputSizeEnforcerTest() {
    super(ParserUtils.ALPHANUM);
  }

  @Test
  public void test1() {
    final Parser<CharSequence> parser = new InputSizeEnforcer<CharSequence>(this.parser, Range.closed(5, 10));
    assertEquals("abcdefg", this.parse(parser, "abcdefg"));
    assertEquals(null, this.parse(parser, "abc", 3));
    assertEquals("abcdefghij", this.parse(parser, "abcdefghijklmnopqrstuvwxyz", 16));
  }

}
