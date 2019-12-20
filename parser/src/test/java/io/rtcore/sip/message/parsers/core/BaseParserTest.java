package io.rtcore.sip.message.parsers.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.ParserUtils;

public class BaseParserTest<T> {

  protected final Parser<T> parser;

  public BaseParserTest(final Parser<T> parser) {
    this.parser = parser;
  }

  protected T parse(final String input) {
    final ByteParserInput is = ByteParserInput.fromString(input);
    final T value = ParserUtils.read(is, this.parser);
    assertEquals(0, is.remaining(), "Parser didn't consume all of the data (returned " + value + ")");
    return value;
  }

  protected T parse(final String input, final int remaining) {
    final ByteParserInput is = ByteParserInput.fromString(input);
    final T value = ParserUtils.read(is, this.parser);
    assertEquals(remaining, is.remaining(), "Parser didn't consume all of the data");
    return value;
  }

  protected <R> R parse(final Parser<R> parser, final String input, final int remain) {
    final ByteParserInput is = ByteParserInput.fromString(input);
    final R value = ParserUtils.read(is, parser);
    assertEquals(remain, is.remaining(), "Parser didn't consume all of the data");
    return value;
  }

  protected <R> R parse(final Parser<R> parser, final String input) {
    return this.parse(parser, input, 0);
  }

}
