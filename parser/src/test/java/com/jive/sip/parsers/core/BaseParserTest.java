package com.jive.sip.parsers.core;

import org.junit.Assert;

import com.jive.sip.parsers.api.Parser;

public class BaseParserTest<T>
{

  protected final Parser<T> parser;

  public BaseParserTest(final Parser<T> parser)
  {
    this.parser = parser;
  }

  protected T parse(final String input)
  {
    final ByteParserInput is = ByteParserInput.fromString(input);
    final T value = ParserUtils.read(is, this.parser);
    Assert.assertEquals("Parser didn't consume all of the data (returned " + value + ")", 0, is.remaining());
    return value;
  }

  protected T parse(final String input, final int remaining)
  {
    final ByteParserInput is = ByteParserInput.fromString(input);
    final T value = ParserUtils.read(is, this.parser);
    Assert.assertEquals("Parser didn't consume all of the data", remaining, is.remaining());
    return value;
  }

  protected <R> R parse(final Parser<R> parser, final String input, final int remain)
  {
    final ByteParserInput is = ByteParserInput.fromString(input);
    final R value = ParserUtils.read(is, parser);
    Assert.assertEquals("Parser didn't consume all of the data", remain, is.remaining());
    return value;
  }

  protected <R> R parse(final Parser<R> parser, final String input)
  {
    return this.parse(parser, input, 0);
  }

}
