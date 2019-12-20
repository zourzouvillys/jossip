/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import io.rtcore.sip.message.message.api.headers.CallId;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParserUtils;

/**
 * 
 *
 */
public class CallIdParser implements Parser<CallId> {

  private static final Parser<CharSequence> WORD =
    ParserUtils.chars(ParserUtils.ALPHANUM_CHARS
      .concat("-.!%*_+`'~()<>:\\\"/[]?{}"));

  private static final Parser<CharSequence> AT = ParserUtils.ch('@');

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.parsers.core.Parser#find(io.rtcore.sip.message.parsers.core.ParserContext,
   * io.rtcore.sip.message.parsers.core.ValueListener)
   */
  @Override
  public boolean find(final ParserContext ctx, final ValueListener<CallId> value) {

    final int pos = ctx.position();

    final CharSequence username = ParserUtils.read(ctx, WORD);

    if (username == null) {
      return false;
    }

    if (ctx.skip(AT)) {
      final CharSequence host = ParserUtils.read(ctx, WORD);
      if (host == null) {
        ctx.position(pos);
        return false;
      }
    }

    if (value != null) {
      value.set(new CallId(ctx.subSequence(pos, ctx.position())));
    }

    return true;
  }

}
