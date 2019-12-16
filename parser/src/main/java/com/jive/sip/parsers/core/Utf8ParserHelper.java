/**
 * 
 */
package com.jive.sip.parsers.core;

import java.util.List;

import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
public class Utf8ParserHelper {
  private static final Parser<CharSequence> UTF8_CONT = new Parser<CharSequence>() {

    @Override
    public boolean find(ParserContext ctx, ValueListener<CharSequence> value) {
      int pos = ctx.position();
      byte b = ctx.peek();

      if (b >= 0x80 && b <= 0xBF) {
        ctx.get();
        ParserHelper.notifyValue(ctx, value, pos);
        return true;
      }

      return false;
    }

    @Override
    public String toString() {
      return "UTF8-CONT";
    }
  };

  private static final Parser<List<CharSequence>> _1UTF8_CONT = ParserUtils.repeat(UTF8_CONT, 1);
  private static final Parser<List<CharSequence>> _2UTF8_CONT = ParserUtils.repeat(UTF8_CONT, 2);
  private static final Parser<List<CharSequence>> _3UTF8_CONT = ParserUtils.repeat(UTF8_CONT, 3);
  private static final Parser<List<CharSequence>> _4UTF8_CONT = ParserUtils.repeat(UTF8_CONT, 4);
  private static final Parser<List<CharSequence>> _5UTF8_CONT = ParserUtils.repeat(UTF8_CONT, 5);

  public static final Parser<CharSequence> UTF8_NONASCII = new Parser<CharSequence>() {

    private boolean check(Parser<?> parser, ParserContext ctx, ValueListener<CharSequence> value, int pos) {
      if (!ctx.skip(parser)) {
        ctx.position(pos);
        return false;
      }
      ParserHelper.notifyValue(ctx, value, pos);
      return true;
    }

    @Override
    public boolean find(ParserContext ctx, ValueListener<CharSequence> value) {
      int pos = ctx.position();

      byte b = ctx.get();
      if (b >= 0xC0 && b <= 0xDF) {
        return check(_1UTF8_CONT, ctx, value, pos);
      }
      else if (b >= 0xE0 && b <= 0xEF) {
        return check(_2UTF8_CONT, ctx, value, pos);
      }
      else if (b >= 0xF0 && b <= 0xF7) {
        return check(_3UTF8_CONT, ctx, value, pos);
      }
      else if (b >= 0xF8 && b <= 0xFB) {
        return check(_4UTF8_CONT, ctx, value, pos);
      }
      else if (b >= 0xFC && b <= 0xFD) {
        return check(_5UTF8_CONT, ctx, value, pos);
      }
      ctx.position(pos);
      return false;
    }

    @Override
    public String toString() {
      return "UTF8-NONASCII";
    }
  };

  private static final Parser<CharSequence> TEXT_UTF8CHAR = new Parser<CharSequence>() {

    @Override
    public boolean find(ParserContext ctx, ValueListener<CharSequence> value) {
      int pos = ctx.position();

      byte b = ctx.get();
      if (b >= 0x21 && b <= 0x7E) {
        ParserHelper.notifyValue(ctx, value, pos);
        return true;
      }

      ctx.position(pos);
      if (!ctx.skip(UTF8_NONASCII)) {
        return false;
      }

      ParserHelper.notifyValue(ctx, value, pos);
      return true;
    }

    @Override
    public String toString() {
      return "TEXT_UTF8CHAR";
    }
  };

  public static final Parser<CharSequence> TEXT_UTF8_TRIM = new Parser<CharSequence>() {

    @Override
    public boolean find(ParserContext ctx, ValueListener<CharSequence> value) {
      int pos = ctx.position();

      if (!ctx.skip(TEXT_UTF8CHAR)) {
        return false;
      }

      while (true) {
        ctx.skip(ParserUtils.LWS);
        if (!ctx.skip(TEXT_UTF8CHAR)) {
          break;
        }
      }

      ParserHelper.notifyValue(ctx, value, pos);
      return true;
    }

    @Override
    public String toString() {
      return "TEXT-UTF8-TRIM";
    }
  };
}
