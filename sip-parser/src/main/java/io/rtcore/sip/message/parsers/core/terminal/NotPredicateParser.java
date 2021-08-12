package io.rtcore.sip.message.parsers.core.terminal;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;

/**
 * 
 * 
 * 
 * @param <T>
 */

public class NotPredicateParser<T> implements Parser<T> {

  private final Parser<T> parser;

  public NotPredicateParser(final Parser<T> parser) {
    this.parser = parser;

  }

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<T> value) {

    final int pos = ctx.position();

    if (this.parser.find(ctx, value)) {
      ctx.position(pos);
      return false;
    }

    return true;

  }

  @Override
  public String toString() {
    return new StringBuilder().append("not(").append(this.parser.toString()).append(")").toString();
  }

}
