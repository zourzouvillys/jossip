package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.RAck;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.parsers.core.ValueHolder;

public class RAckParser implements Parser<RAck> {

  private static final CSeqParser cseq = new CSeqParser();

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<RAck> value) {

    final int pos = ctx.position();

    final UnsignedInteger rseq = ctx.read(ParserUtils.uint(1, 10), null);

    if ((rseq == null) || (ctx.skip(ParserUtils.LWS) == false)) {
      ctx.position(pos);
      return false;
    }

    //
    ValueHolder<CSeq> vcseq = new ValueHolder<CSeq>();

    if (!cseq.find(ctx, vcseq)) {
      ctx.position(pos);
      return false;
    }

    if (value != null) {
      value.set(new RAck(rseq, vcseq.value()));
    }

    return true;

  }

}
