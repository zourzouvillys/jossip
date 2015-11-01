package com.jive.sip.parameters.impl;

import java.util.Optional;

import com.jive.sip.base.api.Token;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.RawParameter;

public class FlagParameterDefinition extends TokenParameterDefinition
{

  public FlagParameterDefinition(final CharSequence name)
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
          return Optional.of(this.name);
        }
      }
    }

    return Optional.empty();
  }

}
