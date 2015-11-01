package com.jive.sip.parsers.core.terminal;

import java.util.Collection;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;


public class AndParser<T> extends NaryParser<T>
{

  private final Collection<Parser<T>> finders;

  public AndParser(final Collection<Parser<T>> finders)
  {
    this.finders = finders;
  }

  @Override
  public Collection<Parser<T>> getElements()
  {

    return this.finders;
  }

  @Override
  public boolean find(final ParserContext context, final ValueListener<T> value)
  {

    final int pos = context.position();

    for (final Parser<T> finder : this.finders)
    {
      if (!finder.find(context, value))
      {
        context.position(pos);
        return false;
      }
    }

    return true;

  }

  @Override
  public String toString()
  {
    return new StringBuilder().append("(").append(
        Joiner.on(" && ").join(FluentIterable.from(Lists.newArrayList(this.finders)).transform(Functions.toStringFunction()))
        ).append(")").toString();
  }
}
