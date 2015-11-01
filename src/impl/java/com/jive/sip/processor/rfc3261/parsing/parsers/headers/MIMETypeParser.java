/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static com.jive.sip.parsers.core.ParserUtils.TOKEN;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.jive.sip.base.api.Token;
import com.jive.sip.message.api.headers.MIMEType;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.tools.ParameterBuilder;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.parsers.core.QuotedStringParser;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class MIMETypeParser implements Parser<MIMEType>
{
  /*
   * (non-Javadoc)
   * @see com.jive.sip.parsers.core.Parser#find(com.jive.sip.parsers.core.ParserContext, com.jive.sip.parsers.core.ValueListener)
   */
  @Override
  public boolean find(ParserContext ctx, ValueListener<MIMEType> value)
  {
    int pos = ctx.position();

    CharSequence type = ParserUtils.read(ctx, ParserUtils.TOKEN);
    if (type == null)
    {
      return false;
    }
    if (!ctx.skip(ParserUtils.SLASH))
    {
      ctx.position(pos);
      return false;
    }
    CharSequence subType = ParserUtils.read(ctx, ParserUtils.TOKEN);
    if (subType == null)
    {
      ctx.position(pos);
      return false;
    }

    Collection<RawParameter> params = Lists.newArrayList();

    while (ctx.skip(ParserUtils.SEMI))
    {
      CharSequence pname = ParserUtils.read(ctx, ParserUtils.TOKEN);
      if (pname == null)
      {
        ctx.position(pos);
        return false;
      }
      if (!ctx.skip(ParserUtils.EQUALS))
      {
        ctx.position(pos);
        return false;
      }
      
      Object authValue = ParserUtils.read(ctx, QuotedStringParser.INSTANCE);
      if (authValue != null)
      {
        params.add(new RawParameter(Token.from(pname), new QuotedStringParameterValue(authValue.toString())));
        continue;
      }
      
      authValue = ParserUtils.read(ctx, TOKEN);
      if (authValue != null)
      {
        params.add(new RawParameter(Token.from(pname), new TokenParameterValue(authValue.toString())));
        continue;
      }
      
      ctx.position(pos);
      return false;
    }
    
    if (value != null)
    {
      value.set(new MIMEType(type.toString(), subType.toString(), ParameterBuilder.from(params)));
    }
    return true;
  }

}
