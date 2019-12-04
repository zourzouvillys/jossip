package com.jive.sip.parameters.impl;

import java.util.Optional;

import com.google.common.net.HostAndPort;
import com.google.common.net.HostSpecifier;
import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.HostAndPortParameterValue;
import com.jive.sip.parameters.api.ParameterValue;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.SipParameterDefinition;
import com.jive.sip.parameters.api.TokenParameterValue;

public class TokenParameterDefinition extends BaseParameterDefinition implements SipParameterDefinition<Token>
{

  public TokenParameterDefinition(final CharSequence name)
  {
    super(name);
  }

  public TokenParameterDefinition(final Token name)
  {
    super(name);
  }

  @Override
  public Optional<Token> parse(final Parameters parameters)
  {
    if (parameters != null)
    {
      for (final RawParameter p : parameters.getRawParameters())
      {
        if (this.matches(p.getName()))
        {
          return Optional.ofNullable(this.convert(p.getValue()));
        }
      }
    }
    return Optional.empty();
  }

  private Token convert(final ParameterValue value)
  {
    if (value instanceof TokenParameterValue)
    {
      return (Token) value.getValue();
    }
    else if (value instanceof QuotedStringParameterValue)
    {
      Token token = null;
      try
      {
        token = Token.from((String) value.getValue());
      }
      catch (final Exception e)
      {
      }

      return token;
    }
    else if (value instanceof HostAndPortParameterValue)
    {
      return Token.from(((HostAndPort) value.getValue()).getHost());
    }

    return null;
  }

  @Override
  public ParameterValue<Token> toParameterValue(final String value)
  {
    return new TokenParameterValue(value);
  }

  @Override
  public ParameterValue<Token> toParameterValue(final HostAndPort value)
  {
    // err, seems like this would fail?
    return new TokenParameterValue(value.toString());
  }

  @Override
  public ParameterValue<Token> toParameterValue(final long value)
  {
    return new TokenParameterValue(value);
  }

}
