/**
 * 
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParserUtils;

/**
 * 1*DIGIT LWS Method
 */

public class CSeqParser implements Parser<CSeq> {

  public static final CSeqParser INSTANCE = new CSeqParser();

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<CSeq> value) {

    final int pos = ctx.position();

    final UnsignedInteger seq = ctx.read(ParserUtils.uint(1, 10), null);

    if ((seq == null) || (ctx.skip(ParserUtils.LWS) == false)) {
      ctx.position(pos);
      return false;
    }

    final CharSequence method = ctx.read(ParserUtils.TOKEN);

    if (method == null) {
      ctx.position(pos);
      return false;
    }

    if (value != null) {
      value.set(new CSeq(seq, SipMethod.of(method)));
    }

    return true;

  }

}
