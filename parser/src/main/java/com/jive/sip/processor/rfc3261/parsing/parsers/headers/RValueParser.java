/**
 *
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static com.jive.sip.parsers.core.ParserUtils.ALPHANUM_CHARS;
import static com.jive.sip.parsers.core.ParserUtils.chars;
import static com.jive.sip.parsers.core.ParserUtils.name;

import com.jive.sip.message.api.headers.RValue;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParserUtils;

/**
 * @author theo
 *
 */

public class RValueParser implements Parser<RValue>
{

  public static final Parser<CharSequence> TOKEN_NODOT = name(chars(ALPHANUM_CHARS.concat("-!%*_+`'~")), "TOKEN-NODOT");

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<RValue> value)
  {

    final CharSequence namespace = ParserUtils.read(ctx, TOKEN_NODOT);

    if (namespace == null)
    {
      return false;
    }

    ctx.read(ParserUtils.ch('.'));

    final CharSequence priority = ParserUtils.read(ctx, TOKEN_NODOT);

    if (priority == null)
    {
      return false;
    }

    if (value != null)
    {
      value.set(new RValue(namespace, priority));
    }

    return true;

  }

}
