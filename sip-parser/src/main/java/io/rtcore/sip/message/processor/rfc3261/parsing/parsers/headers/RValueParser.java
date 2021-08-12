/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.ALPHANUM_CHARS;
import static io.rtcore.sip.message.parsers.core.ParserUtils.chars;
import static io.rtcore.sip.message.parsers.core.ParserUtils.name;

import io.rtcore.sip.message.message.api.headers.RValue;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParserUtils;

/**
 * 
 *
 */

public class RValueParser implements Parser<RValue> {

  public static final Parser<CharSequence> TOKEN_NODOT = name(chars(ALPHANUM_CHARS.concat("-!%*_+`'~")), "TOKEN-NODOT");

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<RValue> value) {

    final CharSequence namespace = ParserUtils.read(ctx, TOKEN_NODOT);

    if (namespace == null) {
      return false;
    }

    ctx.read(ParserUtils.ch('.'));

    final CharSequence priority = ParserUtils.read(ctx, TOKEN_NODOT);

    if (priority == null) {
      return false;
    }

    if (value != null) {
      value.set(new RValue(namespace, priority));
    }

    return true;

  }

}
