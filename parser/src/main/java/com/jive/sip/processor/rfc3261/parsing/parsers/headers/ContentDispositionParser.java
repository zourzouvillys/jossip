package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static com.jive.sip.parsers.core.ParserUtils.TOKEN;

import java.util.Collection;

import com.jive.sip.message.api.ContentDisposition;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.tools.ParameterBuilder;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParameterParser;

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
