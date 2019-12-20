/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.COLON;
import static io.rtcore.sip.message.parsers.core.ParserUtils.LWS;
import static io.rtcore.sip.message.parsers.core.ParserUtils.TERM;
import static io.rtcore.sip.message.parsers.core.ParserUtils.TOKEN;
import static io.rtcore.sip.message.parsers.core.ParserUtils.not;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParseFailureException;

/**
 * 
 *
 */
public class RawHeaderParser implements Parser<RawHeader> {

  private final static Parser<CharSequence> NOT_TERM = not(TERM);

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.parsers.core.Parser#find(io.rtcore.sip.message.parsers.core.ParserContext,
   * io.rtcore.sip.message.parsers.core.ValueListener)
   */
  @Override
  public boolean find(ParserContext ctx, ValueListener<RawHeader> value) {
    int pos = ctx.position();
    try {
      String name = ctx.read(TOKEN).toString();
      ctx.read(COLON);
      int start = ctx.position();
      do {
        while (ctx.skip(NOT_TERM) && ctx.position() < ctx.length()) {
          ctx.get();
        }
      }
      while (ctx.skip(LWS));

      int end = ctx.position();
      ctx.read(TERM);

      if (value != null) {
        value.set(new RawHeader(name, ctx.subSequence(start, end).toString()));
      }
      return true;
    }
    catch (ParseFailureException e) {
      ctx.position(pos);
      return false;
    }
  }

}
