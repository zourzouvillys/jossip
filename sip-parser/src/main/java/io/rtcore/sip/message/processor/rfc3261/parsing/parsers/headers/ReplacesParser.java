/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import java.util.Collection;

import io.rtcore.sip.message.message.api.Replaces;
import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParameterParser;

/**
 * RFC 3326 Replaces header field value.
 */

public class ReplacesParser implements Parser<Replaces> {

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Replaces> value) {

    final CallId callId = ctx.read(new CallIdParser());

    final Collection<RawParameter> params = ctx.read(ParameterParser.getInstance());

    if (value != null) {
      value.set(new Replaces(callId, DefaultParameters.from(params)));
    }

    return true;

  }

}
