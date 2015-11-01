package com.jive.sip.parsers.core.terminal;

import static com.jive.sip.parsers.core.ParserUtils.CRLF;
import static com.jive.sip.parsers.core.ParserUtils.LF;
import static com.jive.sip.parsers.core.ParserUtils.WSP;
import static com.jive.sip.parsers.core.ParserUtils.and;
import static com.jive.sip.parsers.core.ParserUtils.multi;
import static com.jive.sip.parsers.core.ParserUtils.or;

import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParserHelper;

public class LinearWhitespaceParser implements Parser<CharSequence>
{

  @Override
  public boolean find(final ParserContext context, final ValueListener<CharSequence> value)
  {

    final int pos = context.position();

    boolean matched = context.skip(multi(WSP));

    while (context.skip(and(or(CRLF, LF), WSP)))
    {
      context.skip(multi(WSP));
      matched = true;
    }

    if (!matched)
    {
      context.position(pos);
      return false;
    }

    ParserHelper.notifyValue(context, value, pos);
    return true;

  }

  @Override
  public String toString()
  {
    return "LWS";
  }

}
