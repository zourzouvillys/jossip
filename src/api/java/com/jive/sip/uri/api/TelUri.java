package com.jive.sip.uri.api;


import java.util.Optional;
import java.util.function.Function;

import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.parameters.impl.TokenParameterDefinition;

import lombok.EqualsAndHashCode;
import lombok.Value;


@Value
@EqualsAndHashCode(callSuper = true)
public class TelUri extends BaseParameterizedObject<TelUri> implements Uri
{

  private String number;
  private static final TokenParameterDefinition P_TGRP = new TokenParameterDefinition(Token.from("tgrp"));

  public TelUri(final String number)
  {
    this(number, DefaultParameters.EMPTY);
  }

  public TelUri(final String number, final Parameters parameters)
  {
    this.number = number;
    this.parameters = parameters;
  }

  @Override
  public String getScheme()
  {
    return "tel";
  }

  @Override
  public String toString()
  {

    StringBuilder sb = new StringBuilder();

    sb.append(this.getScheme()).append(':');
    sb.append(this.number);

    if (this.parameters != null)
    {
      sb.append(this.parameters);
    }

    return sb.toString();

  }

  @Override
  public <T> T apply(UriVisitor<T> visitor)
  {
    if (visitor instanceof TelUriVisitor)
    {
      return ((TelUriVisitor<T>) visitor).visit(this);
    }
    return visitor.visit(this);
  }

  @Override
  public TelUri withParameters(Parameters parameters)
  {
    return new TelUri(this.number, parameters);
  }

  public TelUri withTrunkGroup(String value)
  {
    Parameters parameters = this.parameters.withParameter(Token.from("tgrp"), Token.from(value));
    return new TelUri(this.number, parameters);
  }


  public Optional<String> getTrunkGroup()
  {
    if (this.parameters != null)
    {
      return parameters.getParameter(P_TGRP).map(new Function<Token, String>()
      {
        @Override
        public String apply(Token input)
        {
          return input.toString();
        }
      });
    }
    return null;
  }

}
