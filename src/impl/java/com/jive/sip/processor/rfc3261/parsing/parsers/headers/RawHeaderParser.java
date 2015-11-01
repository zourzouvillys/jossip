/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static com.jive.sip.parsers.core.ParserUtils.COLON;
import static com.jive.sip.parsers.core.ParserUtils.LWS;
import static com.jive.sip.parsers.core.ParserUtils.TERM;
import static com.jive.sip.parsers.core.ParserUtils.TOKEN;
import static com.jive.sip.parsers.core.ParserUtils.not;

import com.jive.sip.base.api.RawHeader;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParseFailureException;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public class RawHeaderParser implements Parser<RawHeader>
{

  private final static Parser<CharSequence> NOT_TERM = not(TERM);
  
  /* (non-Javadoc)
   * @see com.jive.sip.parsers.core.Parser#find(com.jive.sip.parsers.core.ParserContext, com.jive.sip.parsers.core.ValueListener)
   */
  @Override
  public boolean find(ParserContext ctx, ValueListener<RawHeader> value)
  {
    int pos = ctx.position();
    try
    {
      String name = ctx.read(TOKEN).toString();
      ctx.read(COLON);
      int start = ctx.position();
      do
      {
        while(ctx.skip(NOT_TERM) && ctx.position() < ctx.length())
        {
          ctx.get();
        }
      }
      while (ctx.skip(LWS));
      
      int end = ctx.position();
      ctx.read(TERM);

      if (value != null)
      {
        value.set(new RawHeader(name, ctx.subSequence(start, end).toString()));
      }
      return true;
    }
    catch (ParseFailureException e)
    {
      ctx.position(pos);
      return false;
    }
  }

}
