package com.jive.sip.parsers.core.terminal;

import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;

/**
 * 
 * @author theo
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
