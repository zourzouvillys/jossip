/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.TOKEN;

import java.util.Collection;

import io.rtcore.sip.message.message.api.Reason;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.tools.ParameterBuilder;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParameterParser;

/**
 * RFC 3326 Reason header field value.
 */

public class ReasonParser implements Parser<Reason> {

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Reason> value) {

    // Q.850 ;cause=16;text="Terminated"
    // SIP ;cause=600 ;text="Busy Everywhere"

    final CharSequence protocol = ctx.read(TOKEN);

    final Collection<RawParameter> params = ctx.read(ParameterParser.getInstance());

    if (value != null) {
      value.set(new Reason(protocol, ParameterBuilder.from(params)));
    }

    return true;

  }

}
