package com.jive.sip.parsers.core.terminal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.core.ByteParserInput;
import com.jive.sip.parsers.core.DefaultParserContext;

public class AndParserTest {

  @SuppressWarnings("unchecked")
  @Test
  public void test() {

    final ParserContext context = new DefaultParserContext(ByteParserInput.fromString("ab"));

    assertTrue(new AndParser<CharSequence>(
      Lists.<Parser<CharSequence>>newArrayList(
        new CharactersParser("a"),
        new CharactersParser("b")))
          .find(
            context,
            null));

  }

}
