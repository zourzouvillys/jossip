/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.TOKEN;

import java.util.Collection;

import io.rtcore.sip.message.message.api.EventSpec;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.tools.ParameterBuilder;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParameterParser;

/**
 * RFC 3265 Event header field value.
 */

public class EventParser implements Parser<EventSpec> {

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<EventSpec> value) {

    final CharSequence protocol = ctx.read(TOKEN);

    final Collection<RawParameter> params = ctx.read(ParameterParser.getInstance(), null);

    if (value != null) {
      value.set(new EventSpec(protocol, ParameterBuilder.from(params)));
    }

    return true;

  }

}
