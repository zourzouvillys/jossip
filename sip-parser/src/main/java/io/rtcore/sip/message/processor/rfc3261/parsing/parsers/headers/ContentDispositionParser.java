package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.TOKEN;

import java.util.Collection;

import io.rtcore.sip.message.message.api.ContentDisposition;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.tools.ParameterBuilder;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParameterParser;

public class ContentDispositionParser implements Parser<ContentDisposition> {

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<ContentDisposition> value) {

    // session;required=true

    final CharSequence cx = ctx.read(TOKEN);

    final Collection<RawParameter> params = ctx.read(ParameterParser.getInstance(), ParameterParser.EMPTY_VALUE);

    if (value != null) {
      value.set(new ContentDisposition(cx, ParameterBuilder.from(params)));
    }

    return true;

  }

}
