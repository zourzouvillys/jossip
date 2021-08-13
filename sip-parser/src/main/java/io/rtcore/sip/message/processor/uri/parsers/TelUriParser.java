package io.rtcore.sip.message.processor.uri.parsers;

import java.util.Collection;

import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.impl.DefaultParameters;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ByteParserInput;
import io.rtcore.sip.message.parsers.core.ParameterParser;
import io.rtcore.sip.message.parsers.core.ParserHelper;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.uri.TelUri;

public class TelUriParser implements UriSchemeParser<TelUri> {

  private TelUriParser() {
  }

  public static final TelUriParser TEL = new TelUriParser();

  private static final Parser<CharSequence> PLUS = ParserUtils.ch('+');
  private static final Parser<CharSequence> PHONE_DIGIT_HEX = ParserUtils.chars("1234567890ABCDEF*#-.()");
  private static final Parser<CharSequence> NUMBER_DIGITS = new Parser<>() {
    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {
      final int pos = ctx.position();
      ctx.skip(PLUS);
      if (!ctx.skip(PHONE_DIGIT_HEX)) {
        ctx.position(pos);
        return false;
      }
      ParserHelper.notifyValue(ctx, value, pos);
      return true;
    }

    @Override
    public String toString() {
      return "number-digits";
    }
  };

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<TelUri> value) {
    final int pos = ctx.position();
    final CharSequence number = ParserUtils.read(ctx, NUMBER_DIGITS);
    if (number == null) {
      ctx.position(pos);
      return false;
    }
    final Collection<RawParameter> params = ParserUtils.read(ctx, ParameterParser.getInstance());
    if (value != null) {
      value.set(new TelUri(number.toString(), DefaultParameters.from(params)));
    }
    return true;
  }

  public static TelUri parse(final String input) {
    final ByteParserInput is = ByteParserInput.fromString(input.substring(4));
    final TelUri value = ParserUtils.read(is, TEL);
    if (is.remaining() > 0) {
      throw new RuntimeException("Trailing Garbage in TEL URI");
    }
    return value;
  }
}
