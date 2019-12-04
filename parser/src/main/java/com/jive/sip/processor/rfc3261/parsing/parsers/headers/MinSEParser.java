/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static com.jive.sip.parsers.core.ParserUtils.INTEGER;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.message.api.MinSE;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.tools.ParameterBuilder;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParameterParser;

public class MinSEParser implements Parser<MinSE>
{

  @Override
  public boolean find(ParserContext ctx, ValueListener<MinSE> value)
  {

    final UnsignedInteger seconds = ctx.read(INTEGER);

    if (seconds == null)
    {
      return false;
    }

    final Optional<Collection<RawParameter>> params = ctx.tryRead(ParameterParser.getInstance());

    value.set(new MinSE(Duration.ofSeconds(seconds.longValue()), params.map(
        p -> ParameterBuilder.from(p)).orElse(DefaultParameters.EMPTY)));

    return true;

  }

}
