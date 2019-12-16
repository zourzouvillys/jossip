package com.jive.sip.parsers.core.terminal;

import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;

public class NameParser<T> implements Parser<T> {

  private final Parser<T> parser;
  private final String name;

  public NameParser(final Parser<T> parser, final String name) {
    this.parser = parser;
    this.name = name;
  }

  @Override
  public boolean find(final ParserContext context, final ValueListener<T> value) {
    return this.parser.find(context, value);
  }

  @Override
  public String toString() {
    return this.name;
  }

}
