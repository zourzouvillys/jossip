package io.rtcore.sip.message.parsers.core.terminal;

import com.google.common.collect.Range;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ParserInput;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.DefaultParserContext;
import io.rtcore.sip.message.parsers.core.SubParserInput;

public class InputSizeEnforcer<T> implements Parser<T> {

  private final Parser<T> parser;
  private final Range<Integer> range;

  public InputSizeEnforcer(final Parser<T> parser, final Range<Integer> range) {
    this.parser = parser;
    this.range = range;
  }

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<T> value) {

    final ParserInput is = new SubParserInput(ctx, ctx.position(), Math.min(ctx.remaining(), this.range.upperEndpoint()));

    if (!this.parser.find(new DefaultParserContext(is), value)) {
      return false;
    }

    if (is.position() < this.range.lowerEndpoint()) {
      return false;
    }

    ctx.position(ctx.position() + is.position());

    return true;

  }
}
