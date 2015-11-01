package com.jive.sip.parameters.impl;

import java.util.Optional;

import com.google.common.net.HostAndPort;
import com.jive.sip.parameters.api.HostAndPortParameterValue;
import com.jive.sip.parameters.api.ParameterValue;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.SipParameterDefinition;
import com.jive.sip.parameters.api.TokenParameterValue;

public class HostParameterDefinition extends BaseParameterDefinition implements SipParameterDefinition<HostAndPort>
{

  public HostParameterDefinition(final CharSequence name)
  {
    super(name);
  }

  @Override
  public Optional<HostAndPort> parse(final Parameters parameters)
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

  private HostAndPort convert(final ParameterValue value)
  {
    if (value instanceof HostAndPortParameterValue)
    {
      return ((HostAndPortParameterValue) value).getValue();
    }
    else if ((value instanceof TokenParameterValue) || (value instanceof QuotedStringParameterValue))
    {
      try
      {
        return HostAndPort.fromString(value.getValue().toString());
      }
      catch (final Exception e)
      {
        return null;
      }
    }
    else
    {
      return null;
    }
  }

  @Override
  public ParameterValue<HostAndPort> toParameterValue(final String value)
  {
    return new HostAndPortParameterValue(value);
  }

  @Override
  public ParameterValue<HostAndPort> toParameterValue(final HostAndPort value)
  {
    return new HostAndPortParameterValue(value);
  }

  @Override
  public ParameterValue<HostAndPort> toParameterValue(final long value)
  {
    throw new IllegalArgumentException("Can't convert long to hostport parameter");
  }

}
