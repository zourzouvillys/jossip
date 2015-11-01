package com.jive.sip.parsers.core;

import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;


public class ParserHelper
{

  public static interface ParserPredicate
  {
    boolean matches(final byte b);
  }

  public static final ParserPredicate isAlphaNum = new ParserPredicate()
  {
    @Override
    public boolean matches(final byte b)
    {
      return ParserUtils.isDigit(b) || ParserUtils.isAlpha(b);
    }
  };

  public static boolean notifyValue(final ParserContext ctx, final ValueListener<CharSequence> value, final int pos)
  {

    if (value != null)
    {
      value.set(ctx.subSequence(pos, ctx.position()));
    }

    return true;

  }

  public static void rewindOver(final ParserContext ctx, final char c)
  {
    while (ctx.get(ctx.position() - 1) == c)
    {
      ctx.position(ctx.position() - 1);
    }
  }

  public static boolean is(final ParserPredicate pred, final byte b)
  {
    return pred.matches(b);
  }

}
