package com.jive.sip.parsers.core;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ParserInput;
import com.jive.sip.parsers.api.ValueCollector;
import com.jive.sip.parsers.core.terminal.AndParser;
import com.jive.sip.parsers.core.terminal.CharactersParser;
import com.jive.sip.parsers.core.terminal.InputSizeEnforcer;
import com.jive.sip.parsers.core.terminal.IntegerParser;
import com.jive.sip.parsers.core.terminal.LinearWhitespaceParser;
import com.jive.sip.parsers.core.terminal.MultiParser;
import com.jive.sip.parsers.core.terminal.NameParser;
import com.jive.sip.parsers.core.terminal.NotPredicateParser;
import com.jive.sip.parsers.core.terminal.OneOf;
import com.jive.sip.parsers.core.terminal.OrParser;
import com.jive.sip.parsers.core.terminal.StringParser;
import com.jive.sip.parsers.core.terminal.UnsignedIntegerParser;

public class ParserUtils
{

  public static final String ALPHA_CHARS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final String DIGIT_CHARS = "0123456789";
  public static final String HEXDIGIT_CHARS = DIGIT_CHARS.concat("ABCDEFabcdef");
  public static final String ALPHANUM_CHARS = ALPHA_CHARS.concat(DIGIT_CHARS);

  public static final Parser<CharSequence> CRLF = name(str("\r\n"), "CRLF");
  public static final Parser<CharSequence> LF = name(ch('\n'), "LF");
  public static final Parser<CharSequence> WSP = name(anyOf(new byte[]
      { ' ', '\t' }), "WSP");
  public static final Parser<Integer> _1DIGIT = name(intp(1, 1), "1DIGIT");
  public static final Parser<Integer> _3DIGIT = name(intp(3, 3), "3DIGIT");
  public static final Parser<Integer> _1_3DIGIT = name(intp(1, 3), "1*3DIGIT");
  public static final Parser<UnsignedInteger> INTEGER = name(uint(1, 10), "INTEGER");

  public static final Parser<CharSequence> HEXDIG = name(anyOf(HEXDIGIT_CHARS.getBytes()), "WSP");

  public static final Parser<CharSequence> ALPHA = name(chars(ALPHA_CHARS), "ALPHA");
  public static final Parser<CharSequence> DIGIT = name(anyOf(DIGIT_CHARS.getBytes(StandardCharsets.UTF_8)), "DIGIT");
  public static final Parser<CharSequence> ALPHANUM = chars(ALPHA_CHARS.concat(DIGIT_CHARS));
  public static final Parser<CharSequence> TOKEN = name(chars(ALPHA_CHARS.concat(DIGIT_CHARS).concat("-.!%*_+`'~")),
      "TOKEN");
  public static final Parser<CharSequence> LWS = new LinearWhitespaceParser();
  public static final Parser<CharSequence> SWS = name(optional(LWS), "SWS");
  public static final Parser<CharSequence> SLASH = name(and(SWS, ch('/'), SWS), "SLASH");
  public static final Parser<CharSequence> COLON = name(and(SWS, ch(':'), SWS), "COLON");
  public static final Parser<CharSequence> EQUALS = name(and(SWS, ch('='), SWS), "EQUALS");
  public static final Parser<CharSequence> SEMI = name(and(SWS, ch(';'), SWS), "SEMI");
  public static final Parser<CharSequence> COMMA = name(and(SWS, ch(','), SWS), "COMMA");
  public static final Parser<CharSequence> LAQUOT = name(and(SWS, ch('<')), "LAQUOT");
  public static final Parser<CharSequence> RAQUOT = name(and(ch('>'), SWS), "RAQUOT");
  public static final Parser<CharSequence> LPAREN = name(and(SWS, ch('('), SWS), "LPAREN");
  public static final Parser<CharSequence> RPAREN = name(and(SWS, ch(')'), SWS), "LPAREN");

  public static final Parser<CharSequence> SIP_VERSION = str("SIP/2.0");
  public static final Parser<CharSequence> SP = name(str(" "), "SP");
  public static final Parser<CharSequence> DQUOTE = name(str("\""), "DQUOTE");

  public static final Parser<CharSequence> TERM = or(LF, CRLF);

  /**
   * Returns a parser which matches a single character.
   *
   * @param ch
   * @return
   */

  public static Parser<CharSequence> ch(final char ch)
  {
    return str(Character.toString(ch));
  }

  /**
   * Matches a specific string.
   *
   * @param str
   *          The string to match
   *
   * @return A parser which will match the given string, in full.
   *
   */

  public static Parser<CharSequence> str(final String str)
  {
    return new StringParser(str);
  }

  /**
   * Parses any characters within the given set.
   *
   * chars(xxx) is faster than multi(anyOf(xxx)), as it scans multiple chars without returning an
   * object each char.
   *
   * @param bytes
   * @return
   */

  public static Parser<CharSequence> chars(final String bytes)
  {
    return new CharactersParser(bytes.getBytes(StandardCharsets.UTF_8));
  }

  public static Parser<CharSequence> charSize(final String bytes, final int minDigits, final int maxDigits)
  {
    return new InputSizeEnforcer<CharSequence>(chars(bytes), Range.closed(minDigits, maxDigits));
  }

  public static Parser<UnsignedInteger> uint(final int minDigits, final int maxDigits)
  {
    return new UnsignedIntegerParser(minDigits, maxDigits);
  }

  public static Parser<Integer> intp(final int minDigits, final int maxDigits)
  {
    return new IntegerParser(minDigits, maxDigits);
  }

  public static <T> Parser<T> name(final Parser<T> parser, final String name)
  {
    return new NameParser<T>(parser, name);
  };

  /**
   * Matches one or more of the given items.
   *
   * @param finder
   * @return
   */

  public static <T> Parser<List<T>> multi(final Parser<T> finder)
  {
    return new MultiParser<T, List<T>>(finder, Range.atLeast(1), new CollectionValueCollector<T>());
  }

  public static <T, R> Parser<R> multi(final Parser<T> finder, final ValueCollector<T, R> collector)
  {
    return new MultiParser<T, R>(finder, Range.atLeast(1), collector);
  }

  /**
   * Returns a parser which matches the given parser a specific number of times.
   *
   * @param finder
   * @param count
   * @return
   *
   */

  public static <T> Parser<List<T>> repeat(final Parser<T> finder, final int count)
  {
    Preconditions.checkArgument(count > 0);
    return new MultiParser<T, List<T>>(finder, Range.singleton(count), new CollectionValueCollector<T>());
  }

  public static <T> Parser<T> repeat(final Parser<T> finder, final int count, final ValueCollector<T, T> collector)
  {
    Preconditions.checkArgument(count > 0);
    return new MultiParser<T, T>(finder, Range.singleton(count), collector);
  }

  public static <T> Parser<T> or(final Parser<T>... finders)
  {
    return new OrParser<T>(Lists.newArrayList(finders));
  }

  public static <T> Parser<T> and(final Parser<T>... finders)
  {
    return new AndParser<T>(Lists.newArrayList(finders));
  }

  public static <T> Parser<T> optional(final Parser<T> finders)
  {
    return new MultiParser<T, T>(finders, Range.atMost(1), new SingleValueCollector<T>());
  }

  public static <T> Parser<T> not(final Parser<T> parser)
  {
    return new NotPredicateParser<T>(parser);
  }

  /**
   * Returns true if the current offset contains a byte in the provided 'bytes' parameter.
   *
   * @param bytes
   * @return
   */

  private static Parser<CharSequence> anyOf(final byte[] bytes)
  {
    return new OneOf(bytes);
  }

  public static boolean isDigit(final byte peek)
  {
    return ((peek >= 48) && (peek <= 57));
  }

  /**
   * Returns null if the given parser can't parse the given input.
   *
   * Note that this method does not ensure all of the content is consumed. Pass a ParserContext to
   * read() for that and then check the remaining bytes.
   *
   * @param input
   * @param parser
   * @return
   */

  public static <T> T read(final ParserInput input, final Parser<T> parser)
  {
    final ValueHolder<T> val = ValueHolder.create();
    final ParserContext context = new DefaultParserContext(input);
    if (!parser.find(context, val))
    {
      return null;
    }
    return val.value();
  }

  public static <T> T read(final ParserContext context, final Parser<T> parser)
  {
    final ValueHolder<T> val = ValueHolder.create();
    if (!parser.find(context, val))
    {
      return null;
    }
    return val.value();
  }

  public static boolean isAlpha(final byte b)
  {
    return (b >= 65) && (b <= 122);
  }

  /**
   * A parser which parses a list of items seperated by a seperator.
   *
   * @param parser
   *          the main element parser
   * @param seperator
   *          the seperator parser
   * @param count
   *          the number of times the element must appear
   *
   * @return
   */

  public static <T> Parser<T> list(final Parser<T> parser, final Parser<T> seperator, final int count)
  {
    return and(repeat(and(parser, seperator), count - 1, null), parser);
  }

  /**
   * A greedy parser which consumes everything it can.
   *
   * @return
   */

  public static <T> Parser<CharSequence> all()
  {

    return (ctx, value) ->
    {

      if (value != null)
      {
        value.set(ctx.subSequence(ctx.position(), ctx.position() + ctx.remaining()));
      }

      ctx.position(ctx.position() + ctx.remaining());

      return true;
    };

  }

  /**
   * A greedy parser which consumes everything it can.
   *
   * @return
   */

  public static <T> Parser<String> allString()
  {

    return (ctx, value) ->
    {

      if (value != null)
      {
        value.set(ctx.subSequence(ctx.position(), ctx.position() + ctx.remaining()).toString());
      }

      ctx.position(ctx.position() + ctx.remaining());

      return true;
    };

  }

}
