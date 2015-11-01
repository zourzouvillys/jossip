/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;
import com.jive.sip.message.api.Via;
import com.jive.sip.message.api.ViaProtocol;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.tools.ParameterBuilder;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.HostAndPortParser;
import com.jive.sip.parsers.core.ParameterParser;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.processor.rfc3261.parsing.parsers.ViaProtocolParser;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class ViaParser implements Parser<Via>
{

  public static Parser<ViaProtocol> PROTOCOL_PARSER = new ViaProtocolParser();

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Via> value)
  {

    // SIP/2.0/UDP 10.101.7.30:5060;branch=z9hG4bK4cbb5ffc;rport.

    final ViaProtocol protocol = ctx.read(PROTOCOL_PARSER);

    ctx.skip(ParserUtils.LWS);

    final HostAndPort sentBy = ctx.read(HostAndPortParser.INSTANCE);

    final Collection<RawParameter> params = ctx.remaining() > 0 ? ctx.read(ParameterParser.getInstance()) : Lists.<RawParameter> newArrayList();

    if (value != null)
    {
      value.set(new Via(protocol, sentBy, ParameterBuilder.from(params)));
    }

    return true;

  }

}
