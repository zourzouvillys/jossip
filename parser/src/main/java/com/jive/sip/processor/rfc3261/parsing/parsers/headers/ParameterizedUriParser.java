/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.jive.sip.message.api.headers.ParameterizedUri;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.tools.ParameterBuilder;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParameterParser;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;
import com.jive.sip.processor.rfc3261.parsing.parsers.uri.UriParser;
import com.jive.sip.uri.api.Uri;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class ParameterizedUriParser implements Parser<ParameterizedUri>
{
  /*
   * (non-Javadoc)
   * 
   * @see com.jive.sip.parsers.core.Parser#find(com.jive.sip.parsers.core.ParserContext,
   * com.jive.sip.parsers.core.ValueListener)
   */
  @Override
  public boolean find(final ParserContext ctx, final ValueListener<ParameterizedUri> value)
  {
    final int pos = ctx.position();

    try
    {
      final Uri uri = ctx.read(UriParser.URI_WITHBRACKETS);

      final Collection<RawParameter> params;

      if (ctx.remaining() > 0)
      {
        params = ctx.read(ParameterParser.getInstance());
      }
      else
      {
        params = Lists.newLinkedList();
      }


      if (value != null)
      {
        value.set(new ParameterizedUri(uri, ParameterBuilder.from(params)));
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
