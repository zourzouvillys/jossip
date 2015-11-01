/**
 *
 */
package com.jive.sip.processor.rfc3261.parsing;

import static com.jive.sip.parsers.core.ParserUtils.TERM;
import static com.jive.sip.parsers.core.ParserUtils.not;

import java.nio.ByteBuffer;

import com.jive.sip.base.api.RawHeader;
import com.jive.sip.base.api.RawMessage;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.AbstractParserInput;
import com.jive.sip.parsers.core.DefaultParserContext;
import com.jive.sip.parsers.core.ParseFailureException;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.processor.rfc3261.parsing.parsers.headers.RawHeaderParser;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 */

public final class DefaultRfcMessageParser implements RfcSipMessageParser, Parser<RawMessage>
{

  @Override
  public RawMessage parse(final ByteBuffer buf)
  {

    final ParserContext context = new DefaultParserContext(new AbstractParserInput(buf.remaining())
    {
      @Override
      public byte get(final int index)
      {
        return buf.get(buf.position() + index);
      }
    });

    final RawMessage msg = ParserUtils.read(context, this);

    if (context.remaining() > 0)
    {
      throw new SipMessageParseFailureException(String.format("failed to parse input, %d trailing bytes", context.remaining()));
    }

    return msg;

    // , this);
  }

  @Override
  public RawMessage parse(final byte[] buf, final int length)
  {

    final ParserContext context = new DefaultParserContext(new AbstractParserInput(length)
    {
      @Override
      public byte get(final int index)
      {
        return buf[index];
      }
    });

    final RawMessage msg = ParserUtils.read(context, this);

    if (context.remaining() > 0)
    {
      throw new SipMessageParseFailureException(String.format("failed to parse input, %d trailing bytes", context.remaining()));
    }

    return msg;

    // , this);
  }

  private static final Parser<CharSequence> NOT_TERM = not(TERM);
  private static final Parser<RawHeader> HEADER = new RawHeaderParser();

  /*
   * (non-Javadoc)
   *
   * @see com.jive.sip.parsers.core.Parser#find(com.jive.sip.parsers.core.ParserContext,
   * com.jive.sip.parsers.core.ValueListener)
   */

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<RawMessage> value)
  {

    final int pos = ctx.position();

    try
    {

      while (ctx.skip(NOT_TERM) && (ctx.position() < ctx.length()))
      {
        ctx.get();
      }

      final RawMessage msg = RawMessage.create(ctx.subSequence(pos, ctx.position()).toString());
      ctx.skip(TERM);

      RawHeader header = null;
      do
      {
        try
        {
          header = ctx.read(HEADER);
          msg.addHeader(header);
        }
        catch (final ParseFailureException e)
        {
          break;
        }
      }
      while (ctx.position() < ctx.length());

      ctx.read(TERM);
      final byte[] body = ctx.subSequence(ctx.position(), ctx.length()).toString().getBytes();
      ctx.position(ctx.length());
      msg.setBody(body);

      if (value != null)
      {
        value.set(msg);
      }
      return true;
    }
    catch (final ParseFailureException e)
    {
      ctx.position(pos);
      return false;
    }
  }


}
