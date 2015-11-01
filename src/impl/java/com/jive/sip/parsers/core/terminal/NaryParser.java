package com.jive.sip.parsers.core.terminal;

import java.util.Collection;

import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;

public abstract class NaryParser<T> implements Parser<T>
{

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<T> value)
  {

    return false;
  }

  public abstract Collection<Parser<T>> getElements();

}
