/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import com.jive.sip.message.api.headers.Warning;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.HostAndPortParser;
import com.jive.sip.parsers.core.ParseFailureException;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.parsers.core.QuotedStringParser;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
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
