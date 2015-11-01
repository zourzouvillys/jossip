package com.jive.sip.parameters.impl;

import java.util.Optional;

import com.google.common.net.HostAndPort;
import com.jive.sip.parameters.api.ParameterValue;
import com.jive.sip.parameters.api.Parameters;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.SipParameterDefinition;

public class QuotedStringParameterDefinition extends BaseParameterDefinition implements SipParameterDefinition<String>
{

  public QuotedStringParameterDefinition(final CharSequence name)
  {
    super(name);
  }

  @Override
  public Optional<String> parse(final Parameters parameters)
  {
    for (final RawParameter p : parameters.getRawParameters())
    {
      if (this.matches(p.getName()))
      {
        return Optional.ofNullable(this.convert(p.getValue()));
      }
    }

    return Optional.empty();
  }

  private String convert(final ParameterValue value)
  {
    final Object obj = value.getValue();
    return obj == null ? null : obj.toString();
  }

  @Override
  public ParameterValue<String> toParameterValue(final String value)
  {
    return new QuotedStringParameterValue(value);
  }

  @Override
  public ParameterValue<String> toParameterValue(final HostAndPort value)
  {
    return new QuotedStringParameterValue(value.toString());
  }

  @Override
  public ParameterValue<String> toParameterValue(final long value)
  {
    return new QuotedStringParameterValue(Long.toString(value));
  }

}
