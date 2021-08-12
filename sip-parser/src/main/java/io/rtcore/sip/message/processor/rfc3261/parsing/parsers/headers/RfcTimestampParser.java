/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.INTEGER;
import static io.rtcore.sip.message.parsers.core.ParserUtils.LWS;
import static io.rtcore.sip.message.parsers.core.ParserUtils.ch;
import static io.rtcore.sip.message.parsers.core.ParserUtils.read;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.message.api.headers.RfcTimestamp;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;

/**
 * 
 * 
 */
public class RfcTimestampParser implements Parser<RfcTimestamp> {
  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.parsers.core.Parser#find(io.rtcore.sip.message.parsers.core.ParserContext,
   * io.rtcore.sip.message.parsers.core.ValueListener)
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
