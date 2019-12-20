package io.rtcore.sip.message.parameters.impl;

import io.rtcore.sip.message.parameters.api.FlagParameterValue;
import io.rtcore.sip.message.parameters.api.HostAndPortParameterValue;
import io.rtcore.sip.message.parameters.api.ParameterValueVisitor;
import io.rtcore.sip.message.parameters.api.QuotedStringParameterValue;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;

public class StringParameterExtractor implements ParameterValueVisitor<String> {

  private static final StringParameterExtractor INSTANCE = new StringParameterExtractor();

  @Override
  public String visit(final FlagParameterValue parameter) {
    return "";
  }

  @Override
  public String visit(final TokenParameterValue parameter) {
    return parameter.value().toString();
  }

  @Override
  public String visit(final QuotedStringParameterValue parameter) {
    return parameter.value();
  }

  @Override
  public String visit(final HostAndPortParameterValue parameter) {
    return parameter.toString();
  }

  public static StringParameterExtractor getInstance() {
    return INSTANCE;
  }

}
