package io.rtcore.sip.message.parsers.core.terminal;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.DefaultParserContext;

public class AndParserTest {

  @Test
  public void test() {

    final ParserContext context = new DefaultParserContext(ByteParserInput.fromString("ab"));

    assertTrue(new AndParser<>(
      Lists.<Parser<CharSequence>>newArrayList(
        new CharactersParser("a"),
        new CharactersParser("b")))
          .find(
            context,
            null));

  }

}
