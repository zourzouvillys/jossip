/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static com.jive.sip.parsers.core.ParserUtils._1DIGIT;
import static com.jive.sip.parsers.core.ParserUtils.ch;

import com.jive.sip.message.api.headers.Version;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class VersionParser implements Parser<Version>
{
  /*
   * (non-Javadoc)
   * 
   * @see com.jive.sip.parsers.core.Parser#find(com.jive.sip.parsers.core.ParserContext,
   * com.jive.sip.parsers.core.ValueListener)
   */
  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Version> value)
  {
    final int pos = ctx.position();

    try
    {
      final int major = ctx.read(_1DIGIT);
      ctx.skip(ch('.'));
      final int minor = ctx.read(_1DIGIT);

      if (value != null)
      {
        value.set(new Version(major, minor));
      }

      return true;
    }
    catch (final SipMessageParseFailureException e)
    {
      ctx.position(pos);
      return false;
    }
  }
}
