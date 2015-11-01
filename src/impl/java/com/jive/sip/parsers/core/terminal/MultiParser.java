package com.jive.sip.parsers.core.terminal;

import com.google.common.collect.Range;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueCollector;
import com.jive.sip.parsers.api.ValueListener;

public class MultiParser<T, R> implements Parser<R>
{

  private final Parser<T> finder;
  private final Range<Integer> count;
  private final ValueCollector<T, R> collector;

  public MultiParser(final Parser<T> finder, final Range<Integer> count, final ValueCollector<T, R> collector)
  {
    this.finder = finder;
    this.count = count;
    this.collector = collector;
  }

  public MultiParser(final Parser<T> finder, final Range<Integer> count)
  {
    this(finder, count, null);
  }

  @Override
  public boolean find(final ParserContext context, final ValueListener<R> value)
  {

    final int pos = context.position();

    int loops = 0;

    do
    {

      if (!this.finder.find(context, null))
      {
        break;
      }

      // this.collector.collect(result.value());

      loops++;

    }
    while ((context.remaining() > 0) && this.canSupportMore(loops));

    if (!this.satifiedBy(loops))
    {
      context.position(pos);
      return false;
    }

    if (value != null)
    {
      value.set(this.collector.value());
    }

    return true;

  }

  @Override
  public String toString()
  {
    return new StringBuilder().append("+").append(this.finder.toString()).toString();
  }

  /**
   * @param current
   *          the current number of parsed values.
   * @return true if this parser should try and consume more entries.
   */
  boolean canSupportMore(final int current)
  {
    if (!this.count.hasUpperBound())
    {
      return true;
    }
    return this.count.upperEndpoint() > current;
  }

  boolean satifiedBy(final int loops)
  {
    return this.count.contains(loops);
  }

}
