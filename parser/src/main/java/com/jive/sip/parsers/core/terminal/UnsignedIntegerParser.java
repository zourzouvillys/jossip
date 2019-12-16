package com.jive.sip.parsers.core.terminal;

import com.google.common.base.Preconditions;
import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParserUtils;

public class UnsignedIntegerParser implements Parser<UnsignedInteger> {

  private final int minDigits;
  private final int maxDigits;

  public UnsignedIntegerParser(final int minDigits, final int maxDigits) {
    Preconditions.checkArgument(minDigits > 0, "Min digits must be at least 1");
    Preconditions.checkArgument(maxDigits < 11, "Max digits must be less than 10");
    this.minDigits = minDigits;
    this.maxDigits = maxDigits;
  }

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<UnsignedInteger> value) {

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
      value.set(UnsignedInteger.valueOf((String) ctx.subSequence(pos, pos + skipped)));
    }

    ctx.position(pos + skipped);

    return true;

  }

  @Override
  public String toString() {
    return new StringBuilder().append(this.minDigits).append("*").append(this.maxDigits).append("DIGITS").toString();
  }

}
