package com.jive.sip.parameters.impl;

import com.jive.sip.parameters.api.FlagParameterValue;
import com.jive.sip.parameters.api.HostAndPortParameterValue;
import com.jive.sip.parameters.api.ParameterValueVisitor;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.TokenParameterValue;

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
