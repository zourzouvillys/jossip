package io.rtcore.sip.message.parsers.core.terminal;

import static io.rtcore.sip.message.parsers.core.ParserUtils.CRLF;
import static io.rtcore.sip.message.parsers.core.ParserUtils.LF;
import static io.rtcore.sip.message.parsers.core.ParserUtils.WSP;
import static io.rtcore.sip.message.parsers.core.ParserUtils.and;
import static io.rtcore.sip.message.parsers.core.ParserUtils.multi;
import static io.rtcore.sip.message.parsers.core.ParserUtils.or;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParserHelper;

public class LinearWhitespaceParser implements Parser<CharSequence> {

  @Override
  public boolean find(final ParserContext context, final ValueListener<CharSequence> value) {

    final int pos = context.position();

    boolean matched = context.skip(multi(WSP));

    while (context.skip(and(or(CRLF, LF), WSP))) {
      context.skip(multi(WSP));
      matched = true;
    }

    if (!matched) {
      context.position(pos);
      return false;
    }

    ParserHelper.notifyValue(context, value, pos);
    return true;

  }

  @Override
  public String toString() {
    return "LWS";
  }

}
