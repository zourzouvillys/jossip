/**
 *
 */
package io.rtcore.sip.message.processor.rfc3261.parsing.parsers.headers;

import static io.rtcore.sip.message.parsers.core.ParserUtils.COMMA;
import static io.rtcore.sip.message.parsers.core.ParserUtils.EQUALS;
import static io.rtcore.sip.message.parsers.core.ParserUtils.LWS;
import static io.rtcore.sip.message.parsers.core.ParserUtils.TOKEN;

import java.util.List;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.auth.headers.DigestCredentials;
import io.rtcore.sip.message.base.api.Token;
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
public class AuthorizationParser implements Parser<Authorization> {

  public static final AuthorizationParser INSTANCE = new AuthorizationParser();

  @Override
  public boolean find(final ParserContext ctx, final ValueListener<Authorization> value) {
    final int pos = ctx.position();

    final CharSequence scheme = ParserUtils.read(ctx, TOKEN);
    if (scheme == null) {
      return false;
    }

    final List<RawParameter> params = Lists.newArrayList();
    ctx.skip(LWS);

    do {
      final CharSequence authName = ParserUtils.read(ctx, TOKEN);
      if (authName == null) {
        ctx.position(pos);
        return false;
      }
      if (!ctx.skip(EQUALS)) {
        ctx.position(pos);
        return false;
      }

      Object authValue = ParserUtils.read(ctx, QuotedStringParser.INSTANCE);
      if (authValue != null) {
        params.add(new RawParameter(Token.from(authName), new QuotedStringParameterValue(authValue.toString())));
        continue;
      }

      authValue = ParserUtils.read(ctx, TOKEN);
      if (authValue != null) {
        params.add(new RawParameter(Token.from(authName), new TokenParameterValue(authValue.toString())));
        continue;
      }

      ctx.position(pos);
      return false;
    }
    while (ctx.skip(COMMA));

    if (value != null) {
      if ("digest".equals(scheme.toString().toLowerCase())) {
        value.set(new DigestCredentials(ParameterBuilder.from(params)));
      }
      else {
        value.set(new Authorization(scheme.toString(), ParameterBuilder.from(params)));
      }
    }

    return true;
  }

}
