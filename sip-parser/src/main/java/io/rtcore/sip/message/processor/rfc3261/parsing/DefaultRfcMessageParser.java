/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.parsing;

import static io.rtcore.sip.message.parsers.core.ParserUtils.TERM;
import static io.rtcore.sip.message.parsers.core.ParserUtils.not;

import java.nio.ByteBuffer;

import io.rtcore.sip.message.base.api.RawHeader;
import io.rtcore.sip.message.base.api.RawMessage;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.AbstractParserInput;
import io.rtcore.sip.message.parsers.core.DefaultParserContext;
import io.rtcore.sip.message.parsers.core.ParseFailureException;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers.RawHeaderParser;

/**
 * 
 */

public final class DefaultRfcMessageParser implements RfcSipMessageParser, Parser<RawMessage> {

  @Override
  public RawMessage parse(final ByteBuffer buf) {

    final ParserContext context = new DefaultParserContext(new AbstractParserInput(buf.remaining()) {
      @Override
      public byte get(final int index) {
        return buf.get(buf.position() + index);
      }
    });

    final RawMessage msg = ParserUtils.read(context, this);

    if (context.remaining() > 0) {
      throw new SipMessageParseFailureException(String.format("failed to parse input, %d trailing bytes", context.remaining()));
    }

    return msg;

    // , this);
  }

  @Override
  public RawMessage parse(final byte[] buf, final int length) {

    final ParserContext context = new DefaultParserContext(new AbstractParserInput(length) {
      @Override
      public byte get(final int index) {
        return buf[index];
      }
    });

    final RawMessage msg = ParserUtils.read(context, this);

    if (context.remaining() > 0) {
      throw new SipMessageParseFailureException(String.format("failed to parse input, %d trailing bytes", context.remaining()));
    }

    return msg;

    // , this);
  }

  private static final Parser<CharSequence> NOT_TERM = not(TERM);
  private static final Parser<RawHeader> HEADER = new RawHeaderParser();

  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.parsers.core.Parser#find(io.rtcore.sip.message.parsers.core.ParserContext,
   * io.rtcore.sip.message.parsers.core.ValueListener)
   */

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<RawMessage> value) {

    final int pos = ctx.position();

    try {

      while (ctx.skip(NOT_TERM) && (ctx.position() < ctx.length())) {
        ctx.get();
      }

      final RawMessage msg = RawMessage.create(ctx.subSequence(pos, ctx.position()).toString());
      ctx.skip(TERM);

      RawHeader header = null;
      do {
        try {
          header = ctx.read(HEADER);
          msg.addHeader(header);
        }
        catch (final ParseFailureException e) {
          break;
        }
      }
      while (ctx.position() < ctx.length());

      ctx.read(TERM);
      final byte[] body = ctx.subSequence(ctx.position(), ctx.length()).toString().getBytes();
      ctx.position(ctx.length());
      msg.setBody(body);

      if (value != null) {
        value.set(msg);
      }
      return true;
    }
    catch (final ParseFailureException e) {
      ctx.position(pos);
      return false;
    }
  }

}
