package com.jive.sip.parsers.core.terminal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Range;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.core.BaseParserTest;
import com.jive.sip.parsers.core.ParserUtils;

public class InputSizeEnforcerTest extends BaseParserTest<CharSequence>
{

  public InputSizeEnforcerTest()
  {
    super(ParserUtils.ALPHANUM);
  }

  @Test
  public void test1()
  {
    final Parser<CharSequence> parser = new InputSizeEnforcer<CharSequence>(this.parser, Range.closed(5, 10));
    assertEquals("abcdefg", this.parse(parser, "abcdefg"));
    assertEquals(null, this.parse(parser, "abc", 3));
    assertEquals("abcdefghij", this.parse(parser, "abcdefghijklmnopqrstuvwxyz", 16));
  }

}
