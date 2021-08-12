package io.rtcore.sip.message.parsers.core.terminal;

import java.util.Collection;

import com.google.common.base.Functions;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;

public class OrParser<T> implements Parser<T> {

  private final Collection<Parser<T>> finders;

  public OrParser(final Collection<Parser<T>> finders) {
    this.finders = finders;
  }

  @Override
  public boolean find(final ParserContext context, final ValueListener<T> value) {

    final int pos = context.position();

    for (final Parser<T> finder : this.finders) {
      if (finder.find(context, value)) {
        return true;
      }
    }

    context.position(pos);
    return false;

  }

  @Override
  public String toString() {
    return new StringBuilder().append("(")
      .append(
        Joiner.on(" || ").join(FluentIterable.from(Lists.newArrayList(this.finders)).transform(Functions.toStringFunction())))
      .append(")")
      .toString();
  }

}
