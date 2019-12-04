/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import java.util.Collection;

import com.jive.sip.message.api.Replaces;
import com.jive.sip.message.api.headers.CallId;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParameterParser;

/**
 * RFC 3326 Replaces header field value.
 */

public class ReplacesParser implements Parser<Replaces>
{

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Replaces> value)
  {

    final CallId callId = ctx.read(new CallIdParser());

    final Collection<RawParameter> params = ctx.read(ParameterParser.getInstance());

    if (value != null)
    {
      value.set(new Replaces(callId, DefaultParameters.from(params)));
    }

    return true;

  }

}
