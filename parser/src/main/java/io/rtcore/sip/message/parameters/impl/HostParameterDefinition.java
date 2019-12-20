package io.rtcore.sip.message.parameters.impl;

import java.util.Optional;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.parameters.api.HostAndPortParameterValue;
import io.rtcore.sip.message.parameters.api.ParameterValue;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.QuotedStringParameterValue;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.SipParameterDefinition;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;

public class HostParameterDefinition extends BaseParameterDefinition implements SipParameterDefinition<HostAndPort> {

  public HostParameterDefinition(final CharSequence name) {
    super(name);
  }

  @Override
  public Optional<HostAndPort> parse(final Parameters parameters) {
    if (parameters != null) {
      for (final RawParameter p : parameters.getRawParameters()) {
        if (this.matches(p.name())) {
          return Optional.ofNullable(this.convert(p.value()));
        }
      }
    }
    return Optional.empty();
  }

  private HostAndPort convert(final ParameterValue value) {
    if (value instanceof HostAndPortParameterValue) {
      return ((HostAndPortParameterValue) value).value();
    }
    else if ((value instanceof TokenParameterValue) || (value instanceof QuotedStringParameterValue)) {
      try {
        return HostAndPort.fromString(value.value().toString());
      }
      catch (final Exception e) {
        return null;
      }
    }
    else {
      return null;
    }
  }

  @Override
  public ParameterValue<HostAndPort> toParameterValue(final String value) {
    return new HostAndPortParameterValue(value);
  }

  @Override
  public ParameterValue<HostAndPort> toParameterValue(final HostAndPort value) {
    return new HostAndPortParameterValue(value);
  }

  @Override
  public ParameterValue<HostAndPort> toParameterValue(final long value) {
    throw new IllegalArgumentException("Can't convert long to hostport parameter");
  }

}
