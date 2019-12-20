/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import io.rtcore.sip.message.message.api.headers.Warning;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.HostAndPortParser;
import io.rtcore.sip.message.parsers.core.ParseFailureException;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.parsers.core.QuotedStringParser;

/**
 * 
 * 
 */
public class WarningParser implements Parser<Warning> {

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Warning> value) {

    final int pos = ctx.position();

    try {

      final int code = ctx.read(ParserUtils._3DIGIT);

      if (!ctx.skip(ParserUtils.LWS)) {
        ctx.position(pos);
        return false;
      }

      final CharSequence agent = ctx.read(ParserUtils.or(HostAndPortParser.HOST, ParserUtils.TOKEN));

      if (!ctx.skip(ParserUtils.LWS)) {
        ctx.position(pos);
        return false;
      }

      final CharSequence text = ctx.read(QuotedStringParser.INSTANCE);

      if (value != null) {
        value.set(new Warning(code, agent, text));
      }

      return true;

    }
    catch (final ParseFailureException e) {
      ctx.position(pos);
      return false;
    }

  }

}
