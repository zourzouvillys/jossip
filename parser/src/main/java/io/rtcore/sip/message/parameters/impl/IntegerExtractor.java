package io.rtcore.sip.message.parameters.impl;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.parameters.api.FlagParameterValue;
import io.rtcore.sip.message.parameters.api.HostAndPortParameterValue;
import io.rtcore.sip.message.parameters.api.ParameterValueVisitor;
import io.rtcore.sip.message.parameters.api.QuotedStringParameterValue;
import io.rtcore.sip.message.parameters.api.TokenParameterValue;

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
    return UnsignedInteger.valueOf(parameter.value().toString()).intValue();
  }

  @Override
  public Integer visit(final QuotedStringParameterValue parameter) {
    return UnsignedInteger.valueOf(parameter.value().toString()).intValue();
  }

  @Override
  public Integer visit(final HostAndPortParameterValue parameter) {
    throw new IllegalArgumentException();
  }

}
