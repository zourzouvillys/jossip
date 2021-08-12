package io.rtcore.sip.message.parsers.core.terminal;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;

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
