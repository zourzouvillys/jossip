/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import java.util.Collection;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.message.api.headers.MIMEType;
import io.rtcore.sip.message.parameters.api.QuotedStringParameterValue;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;
import io.rtcore.sip.message.parameters.tools.ParameterBuilder;
import io.rtcore.sip.message.parsers.api.Parser;
import io.rtcore.sip.message.parsers.api.ParserContext;
import io.rtcore.sip.message.parsers.api.ValueListener;
import io.rtcore.sip.message.parsers.core.ParserUtils;
import io.rtcore.sip.message.parsers.core.QuotedStringParser;

/**
 *
 *
 */
public class MIMETypeParser implements Parser<MIMEType> {
  /*
   * (non-Javadoc)
   * @see io.rtcore.sip.message.parsers.core.Parser#find(io.rtcore.sip.message.parsers.core.
   * ParserContext, io.rtcore.sip.message.parsers.core.ValueListener)
   */
  @Override
  public boolean find(final ParserContext ctx, final ValueListener<MIMEType> value) {
    final int pos = ctx.position();

    final CharSequence type = ParserUtils.read(ctx, ParserUtils.TOKEN);
    if (type == null) {
      return false;
    }
    if (!ctx.skip(ParserUtils.SLASH)) {
      ctx.position(pos);
      return false;
    }
    final CharSequence subType = ParserUtils.read(ctx, ParserUtils.TOKEN);
    if (subType == null) {
      ctx.position(pos);
      return false;
    }

    final Collection<RawParameter> params = Lists.newArrayList();

    while (ctx.skip(ParserUtils.SEMI)) {
      final CharSequence pname = ParserUtils.read(ctx, ParserUtils.TOKEN);
      if ((pname == null) || !ctx.skip(ParserUtils.EQUALS)) {
        ctx.position(pos);
        return false;
      }

      Object authValue = ParserUtils.read(ctx, QuotedStringParser.INSTANCE);
      if (authValue != null) {
        params.add(new RawParameter(Token.from(pname), new QuotedStringParameterValue(authValue.toString())));
        continue;
      }

      authValue = ParserUtils.read(ctx, ParserUtils.TOKEN);
      if (authValue != null) {
        params.add(new RawParameter(Token.from(pname), new TokenParameterValue(authValue.toString())));
        continue;
      }

      ctx.position(pos);
      return false;
    }

    if (value != null) {
      value.set(new MIMEType(type.toString(), subType.toString(), ParameterBuilder.from(params)));
    }
    return true;
  }

}
