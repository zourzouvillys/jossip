package com.jive.sip.processor.rfc3261.parsing.parsers;

import static com.jive.sip.parsers.core.ParserUtils.TOKEN;

import java.util.Collection;

import com.jive.sip.message.api.headers.ParameterizedString;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.tools.ParameterBuilder;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParameterParser;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;

public class ParameterizedStringParser implements Parser<ParameterizedString>
{

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<ParameterizedString> value)
  {
    final int pos = ctx.position();
    try
    {

      final CharSequence str = ctx.read(TOKEN);

      final Collection<RawParameter> rp = ctx.read(ParameterParser.getInstance(), ParameterParser.EMPTY_VALUE);

      if (value != null)
      {
        value.set(new ParameterizedString(str.toString(), ParameterBuilder.from(rp)));
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
