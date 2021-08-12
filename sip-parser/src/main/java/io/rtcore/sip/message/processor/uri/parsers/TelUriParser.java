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


  private static final class Parameter {
    private final String name;
    private final String value;

    public Parameter(final String name, final String value) {
      this.name = name;
      this.value = value;
    }

    public String name() {
      return this.name;
    }

    public String value() {
      return this.value;
    }

    @Override
    public boolean equals(final Object o) {
      if (o == this) return true;
      if (!(o instanceof TelUriParser.Parameter)) return false;
      final TelUriParser.Parameter other = (TelUriParser.Parameter) o;
      final Object this$name = this.name();
      final Object other$name = other.name();
      if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
      final Object this$value = this.value();
      final Object other$value = other.value();
      if (this$value == null ? other$value != null : !this$value.equals(other$value)) return false;
      return true;
    }

    @Override
    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $name = this.name();
      result = result * PRIME + ($name == null ? 43 : $name.hashCode());
      final Object $value = this.value();
      result = result * PRIME + ($value == null ? 43 : $value.hashCode());
      return result;
    }

    @Override
    public String toString() {
      return "TelUriParser.Parameter(name=" + this.name() + ", value=" + this.value() + ")";
    }
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
