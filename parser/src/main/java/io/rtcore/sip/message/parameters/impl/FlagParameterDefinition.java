package io.rtcore.sip.message.parameters.impl;

import java.util.Optional;

import io.rtcore.sip.message.base.api.Token;
import io.rtcore.sip.message.parameters.api.Parameters;
import io.rtcore.sip.message.parameters.api.RawParameter;

public class FlagParameterDefinition extends TokenParameterDefinition {

  public FlagParameterDefinition(final CharSequence name) {
    super(name);
  }

  @Override
  public Optional<Token> parse(final Parameters parameters) {
    if (parameters != null) {
      for (final RawParameter p : parameters.getRawParameters()) {
        if (this.matches(p.name())) {
          return Optional.of(this.name);
        }
      }
    }

    return Optional.empty();
  }

}
