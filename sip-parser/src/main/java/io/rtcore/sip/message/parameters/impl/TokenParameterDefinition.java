package io.rtcore.sip.message.parameters.impl;

import java.util.Optional;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.HostAndPortParameterValue;
import io.rtcore.sip.message.parameters.api.ParameterValue;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.QuotedStringParameterValue;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.SipParameterDefinition;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;

public class TokenParameterDefinition extends BaseParameterDefinition implements SipParameterDefinition<Token> {

  public TokenParameterDefinition(final CharSequence name) {
    super(name);
  }

  public TokenParameterDefinition(final Token name) {
    super(name);
  }

  @Override
  public Optional<Token> parse(final Parameters parameters) {
    if (parameters != null) {
      for (final RawParameter p : parameters.getRawParameters()) {
        if (this.matches(p.name())) {
          return Optional.ofNullable(this.convert(p.value()));
        }
      }
    }
    return Optional.empty();
  }

  private Token convert(final ParameterValue<?> value) {
    if (value instanceof TokenParameterValue) {
      return (Token) value.value();
    }
    if (value instanceof QuotedStringParameterValue) {
      Token token = null;
      try {
        token = Token.from((String) value.value());
      }
      catch (final Exception e) {
      }

      return token;
    }
    else if (value instanceof HostAndPortParameterValue) {
      return Token.from(((HostAndPort) value.value()).getHost());
    }

    return null;
  }

  @Override
  public ParameterValue<Token> toParameterValue(final String value) {
    return new TokenParameterValue(value);
  }

  @Override
  public ParameterValue<Token> toParameterValue(final HostAndPort value) {
    // err, seems like this would fail?
    return new TokenParameterValue(value.toString());
  }

  @Override
  public ParameterValue<Token> toParameterValue(final long value) {
    return new TokenParameterValue(value);
  }

}
