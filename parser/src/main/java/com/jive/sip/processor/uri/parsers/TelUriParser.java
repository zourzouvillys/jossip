package com.jive.sip.processor.uri.parsers;

import java.util.Collection;

import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parsers.api.Parser;
import com.jive.sip.parsers.api.ParserContext;
import com.jive.sip.parsers.api.ValueListener;
import com.jive.sip.parsers.core.ByteParserInput;
import com.jive.sip.parsers.core.ParameterParser;
import com.jive.sip.parsers.core.ParserHelper;
import com.jive.sip.parsers.core.ParserUtils;
import com.jive.sip.uri.api.TelUri;

import lombok.Value;

public class TelUriParser implements UriSchemeParser<TelUri> {
  private TelUriParser() {

  }

  public static final TelUriParser TEL = new TelUriParser();

  @Value
  private static class Parameter {
    private String name;
    private String value;
  }

  private static final Parser<CharSequence> PLUS = ParserUtils.ch('+');
  private static final Parser<CharSequence> PHONE_DIGIT_HEX = ParserUtils.chars("1234567890ABCDEF*#-.()");
  private static final Parser<CharSequence> NUMBER_DIGITS = new Parser<CharSequence>() {

    @Override
    public boolean find(final ParserContext ctx, final ValueListener<CharSequence> value) {
      int pos = ctx.position();
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
    int pos = ctx.position();

    CharSequence number = ParserUtils.read(ctx, NUMBER_DIGITS);
    if (number == null) {
      ctx.position(pos);
      return false;
    }
    Collection<RawParameter> params = ParserUtils.read(ctx, ParameterParser.getInstance());

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
