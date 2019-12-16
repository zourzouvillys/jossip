package com.jive.sip.parameters.impl;

import com.google.common.primitives.UnsignedInteger;
import com.jive.sip.parameters.api.FlagParameterValue;
import com.jive.sip.parameters.api.HostAndPortParameterValue;
import com.jive.sip.parameters.api.ParameterValueVisitor;
import com.jive.sip.parameters.api.QuotedStringParameterValue;
import com.jive.sip.parameters.api.TokenParameterValue;

public class IntegerExtractor implements ParameterValueVisitor<Integer> {

  private static IntegerExtractor INSTANCE = new IntegerExtractor();

  public static ParameterValueVisitor<Integer> getInstance() {
    return INSTANCE;
  }

  @Override
  public Integer visit(final FlagParameterValue parameter) {
    throw new IllegalArgumentException();
  }

  @Override
  public Integer visit(final TokenParameterValue parameter) {
    return UnsignedInteger.valueOf(parameter.getValue().toString()).intValue();
  }

  @Override
  public Integer visit(final QuotedStringParameterValue parameter) {
    return UnsignedInteger.valueOf(parameter.getValue().toString()).intValue();
  }

  @Override
  public Integer visit(final HostAndPortParameterValue parameter) {
    throw new IllegalArgumentException();
  }

}
