package io.rtcore.sip.message.parsers.core.terminal;

import com.google.common.base.Preconditions;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParserUtils;

public class IntegerParser implements Parser<Integer> {

  private final int minDigits;
  private final int maxDigits;

  public IntegerParser(final int minDigits, final int maxDigits) {
    Preconditions.checkArgument(minDigits > 0, "Min digits must be at least 1");
    Preconditions.checkArgument(maxDigits < 11, "Max digits must be less than 10");
    this.minDigits = minDigits;
    this.maxDigits = maxDigits;
  }

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Integer> value) {

    final int pos = ctx.position();

    int skipped = 0;

    for (int i = ctx.position(); (i < ctx.limit()) && (skipped < this.maxDigits) && ParserUtils.isDigit(ctx.get(i)); ++i) {
      skipped++;
    }

    if (skipped < this.minDigits) {
      ctx.position(pos);
      return false;
    }

    if (value != null) {
      value.set(Integer.parseInt((String) ctx.subSequence(pos, pos + skipped)));
    }

    ctx.position(pos + skipped);

    return true;

  }

  @Override
  public String toString() {
    return new StringBuilder().append(this.minDigits).append("*").append(this.maxDigits).append("DIGITS").toString();
  }

}
