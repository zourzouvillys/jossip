package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.uri;

import static io.rtcore.sip.message.parsers.core.ParserUtils.ALPHANUM_CHARS;
import static io.rtcore.sip.message.parsers.core.ParserUtils.HEXDIG;
import static io.rtcore.sip.message.parsers.core.ParserUtils.and;
import static io.rtcore.sip.message.parsers.core.ParserUtils.ch;
import static io.rtcore.sip.message.parsers.core.ParserUtils.chars;
import static io.rtcore.sip.message.parsers.core.ParserUtils.name;
import static io.rtcore.sip.message.parsers.core.ParserUtils.or;
import static io.rtcore.sip.message.parsers.core.ParserUtils.str;

import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.ParseFailureException;
import io.rtcore.sip.message.parsers.core.ParserHelper;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.processor.rfc3261.parsing.SipMessageParseFailureException;
import io.rtcore.sip.message.processor.uri.RawUri;
import io.rtcore.sip.message.uri.Uri;

public class UriParser implements Parser<Uri> {

  private UriParser() {
  }

  public static final Parser<CharSequence> SCHEME = chars(ALPHANUM_CHARS + "+-.");

  public static final String RESERVED_CHARS = ";/?:@&=+$,";
  public static final String MARK_CHARS = "-_.!~*'()";
  public static final String UNRESERVED_CHARS = ALPHANUM_CHARS.concat(MARK_CHARS);

  private static final Parser<CharSequence> COLON = ch(':');

  public static final Parser<Uri> URI = new UriParser();

  public static final Parser<Uri> URI_WITHBRACKETS = ParserUtils.name(new Parser<Uri>() {

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

  }, "uri-with-brackets");

  public static final Parser<Uri> URI_WITHOUT_PARAMS = ParserUtils.name(new Parser<Uri>() {
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
          value.set(RawUri.of(scheme.toString(), ctx.subSequence(start, ctx.position()).toString()));
        }

        return true;

      }
      catch (final ParseFailureException e) {
        ctx.position(pos);
        return false;
      }
    }

  }, "uri-without-params");

  public static final Parser<CharSequence> URIC =
    name(or(
      chars(RESERVED_CHARS.concat(UNRESERVED_CHARS)),
      and(str("%"), HEXDIG, HEXDIG)), "uric");

  private static final Parser<CharSequence> OPAQUE = name(new Parser<CharSequence>() {

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

  }, "opaque");

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Uri> value) {
    final int pos = ctx.position();

    try {
      final CharSequence scheme = ctx.read(SCHEME);
      ctx.read(COLON);
      final CharSequence opaque = ctx.read(OPAQUE);

      if (value != null) {
        value.set(RawUri.of(scheme.toString(), opaque.toString()));
      }

      return true;
    }
    catch (final SipMessageParseFailureException e) {
      ctx.position(pos);
      return false;
    }
  }

  public static Uri fromString(String input) {
    final ByteParserInput is = ByteParserInput.fromString(input);
    final Uri value = ParserUtils.read(is, UriParser.URI);
    if (is.remaining() != 0) {
      throw new IllegalArgumentException(String.format("trailing URI characters"));
    }
    return value;
  }

}
