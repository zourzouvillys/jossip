package com.jive.sip.parsers.core.terminal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.jive.sip.parsers.core.BaseParserTest;

public class CharactersParserTest extends BaseParserTest<CharSequence> {

  public CharactersParserTest() {
    super(new CharactersParser("a"));
  }

  @Test
  public void test() {
    assertEquals("a", this.parse("a"));
    assertEquals(null, this.parse(""));
    assertEquals("aaa", this.parse("aaa"));
    assertEquals(null, this.parse(new CharactersParser("a"), "ccd", 3));
    assertEquals("a", this.parse(new CharactersParser("a"), "acd", 2));
  }

}
