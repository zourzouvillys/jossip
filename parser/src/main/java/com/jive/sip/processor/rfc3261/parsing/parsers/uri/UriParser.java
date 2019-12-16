package com.jive.sip.processor.rfc3261.parsing.parsers.uri;

import static com.jive.sip.parsers.core.ParserUtils.ALPHANUM_CHARS;
import static com.jive.sip.parsers.core.ParserUtils.HEXDIG;
import static com.jive.sip.parsers.core.ParserUtils.and;
import static com.jive.sip.parsers.core.ParserUtils.ch;
import static com.jive.sip.parsers.core.ParserUtils.chars;
import static com.jive.sip.parsers.core.ParserUtils.name;
import static com.jive.sip.parsers.core.ParserUtils.or;
import static com.jive.sip.parsers.core.ParserUtils.str;

import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ParseFailureException;
import com.jive.sip.parsers.core.ParserHelper;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.processor.rfc3261.parsing.SipMessageParseFailureException;
import com.jive.sip.processor.uri.RawUri;
import com.jive.sip.uri.api.Uri;

public class UriParser implements Parser<Uri> {
  private UriParser() {
  };

  public static final Parser<CharSequence> SCHEME = chars(ALPHANUM_CHARS + "+-.");

  public static final String RESERVED_CHARS = ";/?:@&=+$,";
  public static final String MARK_CHARS = "-_.!~*'()";
  public static final String UNRESERVED_CHARS = ALPHANUM_CHARS.concat(MARK_CHARS);

  private static final Parser<CharSequence> COLON = ch(':');

  public static final Parser<Uri> URI = new UriParser();

  public static final Parser<Uri> URI_WITHBRACKETS = new Parser<Uri>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<Uri> value) {
      final int pos = ctx.position();

      if (!ctx.skip(ParserUtils.LAQUOT)) {
        return false;
      }

      final Uri uri = ctx.read(URI);
      if (uri == null) {
        ctx.position(pos);
        return false;
      }

      if (!ctx.skip(ParserUtils.RAQUOT)) {
        ctx.position(pos);
        return false;
      }

      if (value != null) {
        value.set(uri);
      }

      return true;
    }
  };

  public static final Parser<Uri> URI_WITHOUT_PARAMS = new Parser<Uri>() {
    @Override
    public boolean find(final ParserContext ctx, final ValueListener<Uri> value) {
      final int pos = ctx.position();

      try {
        final CharSequence scheme = ctx.read(SCHEME);

        ctx.read(COLON);

        // now read all unreserved chars up except '?' and ';', and include HEX chars.

        final int start = ctx.position();

        while (ctx.remaining() > 0) {

          if (!ctx.skip(and(str("%"), HEXDIG, HEXDIG))) {
            if (!ctx.skip(chars(RESERVED_CHARS.concat(UNRESERVED_CHARS).replace("?", "").replace(";", "")))) {
              break;
            }
          }

        }

        if (start == ctx.position()) {
          return false;
        }

        if (value != null) {
          value.set(new RawUri(scheme.toString(), ctx.subSequence(start, ctx.position()).toString()));
        }

        return true;

      }
      catch (final ParseFailureException e) {
        ctx.position(pos);
        return false;
      }
    }
  };

  public static final Parser<CharSequence> URIC =
    name(or(
      chars(RESERVED_CHARS.concat(UNRESERVED_CHARS)),
      and(str("%"), HEXDIG, HEXDIG)), "uric");
  private static final Parser<CharSequence> OPAQUE = new Parser<CharSequence>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {
      final int pos = ctx.position();

      final CharSequence c = ctx.read(URIC);
      if ((c == null) || c.equals("/")) {
        ctx.position(pos);
        return false;
      }

      while (true) {
        if (!ctx.skip(URIC)) {
          break;
        }
      }

      ParserHelper.notifyValue(ctx, value, pos);
      return true;
    }

    @Override
    public String toString() {
      return "opaque";
    }
  };

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Uri> value) {
    final int pos = ctx.position();

    try {
      final CharSequence scheme = ctx.read(SCHEME);
      ctx.read(COLON);
      final CharSequence opaque = ctx.read(OPAQUE);

      if (value != null) {
        value.set(new RawUri(scheme.toString(), opaque.toString()));
      }

      return true;
    }
    catch (final SipMessageParseFailureException e) {
      ctx.position(pos);
      return false;
    }
  }

}
