package io.rtcore.sip.message.parsers.core.terminal;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParserHelper;

public class StringParser implements Parser<CharSequence> {

  private final String str;

  public StringParser(final String str) {
    this.str = str;
  }

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {

    if (ctx.remaining() < this.str.length()) {
      return false;
    }

    final int pos = ctx.position();

    for (int i = 0; i < this.str.length(); ++i) {

      if (ctx.get() != this.str.charAt(i)) {
        ctx.position(pos);
        return false;
      }

    }

    ParserHelper.notifyValue(ctx, value, pos);
    return true;

  }

  @Override
  public String toString() {
    return new StringBuilder().append('"').append(this.str).append('"').toString();
  }

}
