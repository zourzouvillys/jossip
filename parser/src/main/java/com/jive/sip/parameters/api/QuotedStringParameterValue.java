package com.jive.sip.parameters.api;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class QuotedStringParameterValue extends ParameterValue<String> {

  private String value;

  @Override
  public <T> T apply(ParameterValueVisitor<T> visitor) {
    return visitor.visit(this);
  }

  @Override
  public String toString() {
    return new StringBuilder().append('"').append(value.replaceAll("\"", "\\\"")).append('"').toString();
  }

}
