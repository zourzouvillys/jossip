package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.ALPHA_CHARS;
import static io.rtcore.sip.message.parsers.core.ParserUtils.DIGIT_CHARS;
import static io.rtcore.sip.message.parsers.core.ParserUtils.SWS;
import static io.rtcore.sip.message.parsers.core.ParserUtils.chars;
import static io.rtcore.sip.message.parsers.core.ParserUtils.name;
import static io.rtcore.sip.message.parsers.core.ParserUtils.read;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import io.rtcore.sip.message.message.api.NameAddr;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.tools.ParameterBuilder;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.ParameterParser;
import io.rtcore.sip.message.parsers.core.ParseFailureException;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.parsers.core.QuotedStringParser;
import io.rtcore.sip.message.processor.rfc3261.parsing.parsers.uri.UriParser;
import io.rtcore.sip.message.uri.Uri;

/**
 * Tries to parse a name-addr structure defined in RFC 3261.
 *
 * name-addr is one of the more tricky header values. QuotedString is optional, as is a leading set
 * of tokens and LWS. Then, the URI may or may not be in LAQUOT or RAQUOT.
 *
 */

public class NameAddrParser implements Parser<NameAddr> {

  public static final Parser<NameAddr> INSTANCE = new NameAddrParser();

  // we deviate from RFC 3261 to allow some broken chars in the display-name which a number of
  // devices seem to send.
  private static final String BROKEN_CHARS = "()/";

  public static final Parser<CharSequence> DISPLAY_NAME_CHARS =
    name(chars(ALPHA_CHARS.concat(DIGIT_CHARS).concat("-.!%*_+`'~").concat(BROKEN_CHARS)),
      "display-name token");

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<NameAddr> value) {
    final int pos = ctx.position();
    try {
      CharSequence name = read(ctx, QuotedStringParser.INSTANCE);
      if (name == null) {
        final List<CharSequence> parts = Lists.newArrayList();
        int startPos = ctx.position();
        do {
          final CharSequence part = read(ctx, DISPLAY_NAME_CHARS);
          if ((part == null) || UriParser.RESERVED_CHARS.contains(new String(new byte[] { ctx.peek() }))) {
            ctx.position(startPos);
            break;
          }
          parts.add(part);
          ctx.skip(SWS);
          startPos = ctx.position();
        }
        while (ctx.peek() != ':');

        if (!parts.isEmpty()) {
          name = Joiner.on(" ").join(parts);
        }
      }

      Uri uri;
      if (name == null) {
        ctx.skip(SWS);
        if (ctx.peek() == '<') {
          uri = ctx.read(UriParser.URI_WITHBRACKETS);
        }
        else {
          uri = ctx.read(UriParser.URI_WITHOUT_PARAMS);
        }
      }
      else {
        uri = ctx.read(UriParser.URI_WITHBRACKETS);
      }

      final Collection<RawParameter> rawParams =
        (ctx.remaining() > 0) ? ctx.read(ParameterParser.getInstance())
                              : ParameterParser.EMPTY_VALUE;

      if (value != null) {
        NameAddr parsedValue = new NameAddr(uri);
        if (name != null) {
          parsedValue = parsedValue.withName(name.toString());
        }
        if ((rawParams != null) && !rawParams.isEmpty()) {
          parsedValue = parsedValue.withParameters(ParameterBuilder.from(rawParams));
        }
        value.set(parsedValue);
      }

      return true;
    }
    catch (final ParseFailureException e) {
      ctx.position(pos);
      return false;
    }

  }

  public static NameAddr parse(final String input) {
    final ByteParserInput is = ByteParserInput.fromString(input);
    final NameAddr value = ParserUtils.read(is, NameAddrParser.INSTANCE);
    if (is.remaining() > 0) {
      throw new RuntimeException("Trailing Garbage in NameAddr");
    }
    return value;
  }

  public static List<NameAddr> parseList(final String input) {
    final ByteParserInput is = ByteParserInput.fromString(input);    
    final List<NameAddr> value = ParserUtils.read(is, ParserUtils.commaSeparated(NameAddrParser.INSTANCE));
    if (is.remaining() > 0) {
      throw new RuntimeException("Trailing Garbage in NameAddr");
    }
    return value;
  }

}
