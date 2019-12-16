package com.jive.sip.parsers.core;

import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;

public class QuotedStringParser implements Parser<CharSequence> {

  public static final QuotedStringParser INSTANCE = new QuotedStringParser();

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {

    final int pos = ctx.position();

    try {

      ctx.skip(ParserUtils.SWS);

      ctx.read(ParserUtils.DQUOTE);

      final int start = ctx.position();

      // read everything except backslash and double quote and space. LWS is allowed, as is an
      // escaped char, as well as
      // UTF8-NONASCII.

      int i = ctx.position();

      for (i = ctx.position(); i < ctx.limit(); ++i) {

        if (ctx.get(i) == '"') {
          break;
        }
        else if (ctx.get(i) == '\\') {
          i++;
        }

      }

      if (i > ctx.position()) {
        ctx.position(i);
      }

      final int end = ctx.position();

      ctx.read(ParserUtils.DQUOTE);

      if (value != null) {
        value.set(ctx.subSequence(start, end));
      }

      return true;

    }
    catch (final Exception e) {
      ctx.position(pos);
      return false;
    }

  }

  @Override
  public String toString() {
    return "quoted-string";
  }

}
