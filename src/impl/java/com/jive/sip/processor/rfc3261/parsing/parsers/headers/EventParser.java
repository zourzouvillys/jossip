/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static com.jive.sip.parsers.core.ParserUtils.TOKEN;

import java.util.Collection;

import com.jive.sip.message.api.EventSpec;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.tools.ParameterBuilder;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParameterParser;

/**
 * RFC 3265 Event header field value.
 */

public class EventParser implements Parser<EventSpec>
{

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<EventSpec> value)
  {

    final CharSequence protocol = ctx.read(TOKEN);

    final Collection<RawParameter> params = ctx.read(ParameterParser.getInstance(), null);

    if (value != null)
    {
      value.set(new EventSpec(protocol, ParameterBuilder.from(params)));
    }

    return true;

  }

}
