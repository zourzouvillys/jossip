/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.INTEGER;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.message.api.MinSE;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parameters.tools.ParameterBuilder;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParameterParser;

public class MinSEParser implements Parser<MinSE> {

  @Override
  public boolean find(ParserContext ctx, ValueListener<MinSE> value) {

    final UnsignedInteger seconds = ctx.read(INTEGER);

    if (seconds == null) {
      return false;
    }

    final Optional<Collection<RawParameter>> params = ctx.tryRead(ParameterParser.getInstance());

    value.set(new MinSE(
      Duration.ofSeconds(seconds.longValue()),
      params.map(
        p -> ParameterBuilder.from(p)).orElse(DefaultParameters.EMPTY)));

    return true;

  }

}
