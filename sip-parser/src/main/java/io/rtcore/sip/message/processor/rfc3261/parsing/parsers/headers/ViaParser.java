/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import java.util.Collection;

import com.google.common.collect.Lists;
import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.tools.ParameterBuilder;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.HostAndPortParser;
import io.rtcore.sip.message.parsers.core.ParameterParser;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.ViaProtocolParser;

/**
 * 
 * 
 */
public class ViaParser implements Parser<Via> {

  public static final ViaParser INSTANCE = new ViaParser();

  public static Parser<ViaProtocol> PROTOCOL_PARSER = new ViaProtocolParser();

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Via> value) {

    // SIP/2.0/UDP 10.101.7.30:5060;branch=z9hG4bK4cbb5ffc;rport.

    final ViaProtocol protocol = ctx.read(PROTOCOL_PARSER);

    ctx.skip(ParserUtils.LWS);

    final HostAndPort sentBy = ctx.read(HostAndPortParser.INSTANCE);

    final Collection<RawParameter> params =
      ctx.remaining() > 0 ? ctx.read(ParameterParser.getInstance())
                          : Lists.<RawParameter>newArrayList();

    if (value != null) {
      value.set(new Via(protocol, sentBy, ParameterBuilder.from(params)));
    }

    return true;

  }

}
