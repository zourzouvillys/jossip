package io.rtcore.sip.message.parameters.impl;

import java.util.Optional;

import com.google.common.net.HostAndPort;

import io.rtcore.sip.message.parameters.api.ParameterValue;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.QuotedStringParameterValue;
import io.rtcore.sip.message.parameters.api.RawParameter;
import io.rtcore.sip.message.parameters.api.SipParameterDefinition;

public class QuotedStringParameterDefinition extends BaseParameterDefinition implements SipParameterDefinition<String> {

  public QuotedStringParameterDefinition(final CharSequence name) {
    super(name);
  }

  @Override
  public Optional<String> parse(final Parameters parameters) {
    for (final RawParameter p : parameters.getRawParameters()) {
      if (this.matches(p.name())) {
        return Optional.ofNullable(this.convert(p.value()));
      }
    }
    return Optional.empty();
  }

  private String convert(final ParameterValue value) {
    final Object obj = value.value();
    return obj == null ? null
                       : obj.toString();
  }

  @Override
  public ParameterValue<String> toParameterValue(final String value) {
    return new QuotedStringParameterValue(value);
  }

  @Override
  public ParameterValue<String> toParameterValue(final HostAndPort value) {
    return new QuotedStringParameterValue(value.toString());
  }

  @Override
  public ParameterValue<String> toParameterValue(final long value) {
    return new QuotedStringParameterValue(Long.toString(value));
  }

}
