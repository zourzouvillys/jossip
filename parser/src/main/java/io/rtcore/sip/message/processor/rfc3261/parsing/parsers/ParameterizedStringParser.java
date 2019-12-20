package io.rtcore.sip.message.processor.rfc3261.parsing.parsers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.TOKEN;

import java.util.Collection;

import io.rtcore.sip.message.message.api.headers.ParameterizedString;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.tools.ParameterBuilder;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParameterParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;

public class ParameterizedStringParser implements Parser<ParameterizedString> {

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<ParameterizedString> value) {
    final int pos = ctx.position();
    try {

      final CharSequence str = ctx.read(TOKEN);

      final Collection<RawParameter> rp = ctx.read(ParameterParser.getInstance(), ParameterParser.EMPTY_VALUE);

      if (value != null) {
        value.set(new ParameterizedString(str.toString(), ParameterBuilder.from(rp)));
      }

      return true;

    }
    catch (final SipMessageParseFailureException e) {
      ctx.position(pos);
      return false;
    }
  }

}
