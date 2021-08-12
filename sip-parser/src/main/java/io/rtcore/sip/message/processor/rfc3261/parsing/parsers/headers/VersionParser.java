/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static io.rtcore.sip.message.parsers.core.ParserUtils._1DIGIT;
import static io.rtcore.sip.message.parsers.core.ParserUtils.ch;

import io.rtcore.sip.message.message.api.headers.Version;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;

/**
 * 
 * 
 */
public class VersionParser implements Parser<Version> {
  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.parsers.core.Parser#find(io.rtcore.sip.message.parsers.core.ParserContext,
   * io.rtcore.sip.message.parsers.core.ValueListener)
   */
  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Version> value) {
    final int pos = ctx.position();

    try {
      final int major = ctx.read(_1DIGIT);
      ctx.skip(ch('.'));
      final int minor = ctx.read(_1DIGIT);

      if (value != null) {
        value.set(new Version(major, minor));
      }

      return true;
    }
    catch (final SipMessageParseFailureException e) {
      ctx.position(pos);
      return false;
    }
  }
}
