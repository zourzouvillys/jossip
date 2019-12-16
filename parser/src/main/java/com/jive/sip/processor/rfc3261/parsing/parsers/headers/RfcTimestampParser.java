/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static com.jive.sip.parsers.core.ParserUtils.INTEGER;
import static com.jive.sip.parsers.core.ParserUtils.LWS;
import static com.jive.sip.parsers.core.ParserUtils.ch;
import static com.jive.sip.parsers.core.ParserUtils.read;

import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.message.api.headers.RfcTimestamp;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class RfcTimestampParser implements Parser<RfcTimestamp> {
  /*
   * (non-Javadoc)
   * @see com.jive.sip.parsers.core.Parser#find(com.jive.sip.parsers.core.ParserContext,
   * com.jive.sip.parsers.core.ValueListener)
   */
  @Override
  public boolean find(final ParserContext ctx, final ValueListener<RfcTimestamp> value) {
    final int pos = ctx.position();
    try {
      final UnsignedInteger part1 = ctx.read(INTEGER);
      UnsignedInteger part2 = null, part3 = null, part4 = null;

      if (ctx.skip(ch('.'))) {
        part2 = read(ctx, INTEGER);
      }

      if (ctx.skip(LWS)) {
        part3 = ctx.read(INTEGER);

        if (ctx.skip(ch('.'))) {
          part4 = ParserUtils.read(ctx, INTEGER);
        }
      }

      if (value != null) {
        value.set(new RfcTimestamp(
          part1.intValue(),
          (part2 == null) ? null
                          : part2.intValue(),
          (part3 == null) ? null
                          : part3.intValue(),
          (part4 == null) ? null
                          : part4.intValue()));
      }

      return true;
    }
    catch (final SipMessageParseFailureException e) {
      ctx.position(pos);
      return false;
    }
  }
}
