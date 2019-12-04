/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.message.api.CSeq;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParserUtils;

/**
 * 1*DIGIT LWS Method
 */

public class CSeqParser implements Parser<CSeq>
{

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<CSeq> value)
  {
    final int pos = ctx.position();

    final UnsignedInteger seq = ctx.read(ParserUtils.uint(1, 10), null);

    if ((seq == null) || (ctx.skip(ParserUtils.LWS) == false))
    {
      ctx.position(pos);
      return false;
    }

    final CharSequence method = ctx.read(ParserUtils.TOKEN);

    if (method == null)
    {
      ctx.position(pos);
      return false;
    }

    if (value != null)
    {
      value.set(new CSeq(seq, SipMethod.of(method)));
    }

    return true;

  }

}
