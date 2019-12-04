/**
 *
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static com.jive.sip.parsers.core.ParserUtils.COMMA;
import static com.jive.sip.parsers.core.ParserUtils.EQUALS;
import static com.jive.sip.parsers.core.ParserUtils.LWS;
import static com.jive.sip.parsers.core.ParserUtils.TOKEN;

import java.util.List;

import com.google.common.collect.Lists;
import com.jive.sip.auth.headers.Authorization;
import com.jive.sip.auth.headers.DigestCredentials;
import com.jive.sip.base.api.Token;
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
public class AuthorizationParser implements Parser<Authorization>
{
  /*
   * (non-Javadoc)
   *
   * @see com.jive.sip.parsers.core.Parser#find(com.jive.sip.parsers.core.ParserContext, com.jive.sip.parsers.core.ValueListener)
   */

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Authorization> value)
  {
    final int pos = ctx.position();

    final CharSequence scheme = ParserUtils.read(ctx, TOKEN);
    if (scheme == null)
    {
      return false;
    }

    final List<RawParameter> params = Lists.newArrayList();
    ctx.skip(LWS);

    do
    {
      final CharSequence authName = ParserUtils.read(ctx, TOKEN);
      if (authName == null)
      {
        ctx.position(pos);
        return false;
      }
      if (!ctx.skip(EQUALS))
      {
        ctx.position(pos);
        return false;
      }

      Object authValue = ParserUtils.read(ctx, QuotedStringParser.INSTANCE);
      if (authValue != null)
      {
        params.add(new RawParameter(Token.from(authName), new QuotedStringParameterValue(authValue.toString())));
        continue;
      }

      authValue = ParserUtils.read(ctx, TOKEN);
      if (authValue != null)
      {
        params.add(new RawParameter(Token.from(authName), new TokenParameterValue(authValue.toString())));
        continue;
      }

      ctx.position(pos);
      return false;
    } while (ctx.skip(COMMA));

    if (value != null)
    {
      if ("digest".equals(scheme.toString().toLowerCase()))
      {
        value.set(new DigestCredentials(ParameterBuilder.from(params)));
      }
      else
      {
        value.set(new Authorization(scheme.toString(), ParameterBuilder.from(params)));
      }
    }

    return true;
  }

}
