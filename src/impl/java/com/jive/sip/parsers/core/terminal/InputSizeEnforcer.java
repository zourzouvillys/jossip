package com.jive.sip.parsers.core.terminal;

import com.google.common.collect.Range;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ParserInput;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.DefaultParserContext;
import com.jive.sip.parsers.core.SubParserInput;

public class InputSizeEnforcer<T> implements Parser<T>
{

  private final Parser<T> parser;
  private final Range<Integer> range;

  public InputSizeEnforcer(final Parser<T> parser, final Range<Integer> range)
  {
    this.parser = parser;
    this.range = range;
  }

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<T> value)
  {

    final ParserInput is = new SubParserInput(ctx, ctx.position(), Math.min(ctx.remaining(), this.range.upperEndpoint()));

    if (!this.parser.find(new DefaultParserContext(is), value))
    {
      return false;
    }

    if (is.position() < this.range.lowerEndpoint())
    {
      return false;
    }

    ctx.position(ctx.position() + is.position());

    return true;

  }
}
