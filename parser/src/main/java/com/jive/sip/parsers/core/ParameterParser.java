package com.jive.sip.parsers.core;

import static com.jive.sip.parsers.core.ParserUtils.EQUALS;
import static com.jive.sip.parsers.core.ParserUtils.SEMI;
import static com.jive.sip.parsers.core.ParserUtils.SWS;
import static com.jive.sip.parsers.core.ParserUtils.TOKEN;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.jive.sip.parameters.api.FlagParameterValue;
import com.jive.sip.parameters.api.HostAndPortParameterValue;
import com.jive.sip.parameters.api.ParameterValue;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;

/**
 * Parses parameters.
 * 
 * @author theo
 * 
 */

public class ParameterParser implements Parser<Collection<RawParameter>>
{

  public static final Collection<RawParameter> EMPTY_VALUE = Collections.emptyList();

  private static ParameterParser instance = new ParameterParser();

  // use getInstance() instead.
  private ParameterParser()
  {

  }

  public static ParameterParser getInstance()
  {
    return instance;
  }

  @Override
  public String toString()
  {
    return "parameters";
  }

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Collection<RawParameter>> value)
  {
    final int pos = ctx.position();
    final List<RawParameter> params = Lists.newArrayList();

    try
    {
      ParameterValue<?> parsedValue = null;
      while (ctx.skip(SEMI))
      {
        final CharSequence pname = ctx.read(TOKEN);
        CharSequence pvalue = null;
        ctx.skip(SWS);
        if (ctx.skip(EQUALS))
        {
          int tmpPos = ctx.position();
          if ((pvalue = ParserUtils.read(ctx, QuotedStringParser.INSTANCE)) != null)
          {
            parsedValue = new QuotedStringParameterValue(pvalue.toString());
          }
          else if ((pvalue = ParserUtils.read(ctx, TOKEN)) != null)
          {
            // We check for a colon because that's an indicator that the parameter is acutally a host and port
            if (ctx.position() == ctx.length() || ctx.peek() != ':')
            {
              parsedValue = new TokenParameterValue(pvalue.toString());
            }
            else if ((pvalue = ParserUtils.read(ctx.position(tmpPos), HostAndPortParser.AS_CHAR_SEQUENCE)) != null)
            {
              parsedValue = new HostAndPortParameterValue(pvalue.toString());
            }
          }
          else if ((pvalue = ParserUtils.read(ctx, HostAndPortParser.AS_CHAR_SEQUENCE)) != null)
          {
            parsedValue = new HostAndPortParameterValue(pvalue.toString());
          }
        }
        else
        {
          parsedValue = new FlagParameterValue();
        }

        params.add(new RawParameter(pname, parsedValue));

      }
    }
    catch (final ParseFailureException e)
    {
      ctx.position(pos);
      return false;
    }

    if (value != null)
    {
      value.set(params);
    }

    return true;
  }
}
