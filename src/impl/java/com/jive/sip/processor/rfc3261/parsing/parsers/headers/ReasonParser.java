/**
 *
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static com.jive.sip.parsers.core.ParserUtils.TOKEN;

import java.util.Collection;

import com.jive.sip.message.api.Reason;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.tools.ParameterBuilder;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParameterParser;

/**
 * RFC 3326 Reason header field value.
 */

public class ReasonParser implements Parser<Reason>
{

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Reason> value)
  {

    // Q.850 ;cause=16;text="Terminated"
    // SIP ;cause=600 ;text="Busy Everywhere"

    final CharSequence protocol = ctx.read(TOKEN);

    final Collection<RawParameter> params = ctx.read(ParameterParser.getInstance());

    if (value != null)
    {
      value.set(new Reason(protocol, ParameterBuilder.from(params)));
    }

    return true;

  }

}
