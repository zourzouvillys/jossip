package io.rtcore.sip.message.parsers.core.terminal;

import java.util.Collection;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;

public abstract class NaryParser<T> implements Parser<T> {

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<T> value) {

    return false;
  }

  public abstract Collection<Parser<T>> getElements();

}
