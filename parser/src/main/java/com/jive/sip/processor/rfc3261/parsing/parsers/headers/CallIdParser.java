/**
 *
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import com.jive.sip.message.api.headers.CallId;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParserUtils;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 *
 */
public class CallIdParser implements Parser<CallId> {

  private static final Parser<CharSequence> WORD =
    ParserUtils.chars(ParserUtils.ALPHANUM_CHARS
      .concat("-.!%*_+`'~()<>:\\\"/[]?{}"));

  private static final Parser<CharSequence> AT = ParserUtils.ch('@');

  /*
   * (non-Javadoc)
   * @see com.jive.sip.parsers.core.Parser#find(com.jive.sip.parsers.core.ParserContext,
   * com.jive.sip.parsers.core.ValueListener)
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
