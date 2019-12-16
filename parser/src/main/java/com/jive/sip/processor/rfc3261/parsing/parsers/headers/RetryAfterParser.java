/**
 * 
 */
package com.jive.sip.processor.rfc3261.parsing.parsers.headers;

import static com.jive.sip.parsers.core.ParserUtils.TOKEN;
import static com.jive.sip.parsers.core.Utf8ParserHelper.UTF8_NONASCII;

import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.base.api.Token;
import com.jive.sip.message.api.headers.RetryAfter;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.tools.ParameterBuilder;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParserHelper;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.parsers.core.QuotedStringParser;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class RetryAfterParser implements Parser<RetryAfter> {

  private static final Parser<CharSequence> CTEXT = new Parser<CharSequence>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {
      final int pos = ctx.position();

      if (ctx.length() == pos) {
        return false;
      }

      final byte b = ctx.peek();

      if ((b > 0x20) && (b < 0x7F)) {
        // Allowed ASCII characters range
        if (((b > 0x27) && (b < 0x2A)) || (b == 0x5C)) {
          // Exempt characters
          ctx.position(pos);
          return false;
        }
        ctx.get();
        ParserHelper.notifyValue(ctx, value, pos);
        return true;
      }

      if (ctx.skip(UTF8_NONASCII) || ctx.skip(ParserUtils.LWS)) {
        ParserHelper.notifyValue(ctx, value, pos);
        return true;
      }

      return false;
    }

    @Override
    public String toString() {
      return "ctext";
    }
  };

  private static final Parser<CharSequence> SLASH = ParserUtils.ch('\\');
  private static final Parser<CharSequence> QUOTED_PAIR = new Parser<CharSequence>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {
      final int pos = ctx.position();

      if (!ctx.skip(SLASH)) {
        return false;
      }

      final byte b = ctx.get();
      if (((b > 0x09) && (b < 0x0B)) || (b == 0x0D) || (b > 0x7F)) {
        ctx.position(pos);
        return false;
      }

      ParserHelper.notifyValue(ctx, value, pos);
      return true;
    }

    @Override
    public String toString() {
      return "quoted-pair";
    }
  };

  private static final Parser<CharSequence> LPAREN = ParserUtils.ch('(');
  private static final Parser<CharSequence> RPAREN = ParserUtils.ch(')');

  /*
   * (non-Javadoc)
   * @see com.jive.sip.parsers.core.Parser#find(com.jive.sip.parsers.core.ParserContext,
   * com.jive.sip.parsers.core.ValueListener)
   */
  private static final Parser<CharSequence> COMMENT = new Parser<CharSequence>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {
      final int pos = ctx.position();

      if (!ctx.skip(LPAREN)) {
        return false;
      }

      while (true) {
        if (!ctx.skip(CTEXT) && !ctx.skip(QUOTED_PAIR) && !ctx.skip(COMMENT)) {
          break;
        }
      }

      if (!ctx.skip(RPAREN)) {
        ctx.position(pos);
        return false;
      }

      ParserHelper.notifyValue(ctx, value, pos);

      return true;
    }

    @Override
    public String toString() {
      return "comment";
    }
  };

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<RetryAfter> value) {
    final int pos = ctx.position();

    final UnsignedInteger duration = ParserUtils.read(ctx, ParserUtils.INTEGER);
    if (duration == null) {
      return false;
    }

    ctx.skip(ParserUtils.LWS);

    final CharSequence comment = ParserUtils.read(ctx, COMMENT);

    final List<RawParameter> params = Lists.newArrayList();
    while (ctx.skip(ParserUtils.SEMI)) {
      final CharSequence pname = ParserUtils.read(ctx, ParserUtils.TOKEN);
      if (pname == null) {
        ctx.position(pos);
        return false;
      }
      if (!ctx.skip(ParserUtils.EQUALS)) {
        ctx.position(pos);
        return false;
      }

      Object pvalue = ParserUtils.read(ctx, QuotedStringParser.INSTANCE);
      if (pvalue != null) {
        params.add(new RawParameter(Token.from(pname), new QuotedStringParameterValue(pvalue.toString())));
        continue;
      }

      pvalue = ParserUtils.read(ctx, TOKEN);
      if (pvalue != null) {
        params.add(new RawParameter(Token.from(pname), new TokenParameterValue(pvalue.toString())));
        continue;
      }

      ctx.position(pos);
      return false;
    }

    final String cmt =
      comment == null ? null
                      : comment.toString();

    if (value != null) {
      value.set(new RetryAfter(duration.intValue(), cmt, ParameterBuilder.from(params)));
    }
    return true;
  }
}
