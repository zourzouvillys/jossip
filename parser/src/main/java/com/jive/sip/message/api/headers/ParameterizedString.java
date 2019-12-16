package com.jive.sip.message.api.headers;

import com.jive.sip.parameters.api.BaseParameterizedObject;
import com.jive.sip.parameters.api.Parameters;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class ParameterizedString extends BaseParameterizedObject<ParameterizedString> {

  private String value;

  public ParameterizedString(String value) {
    this(value, null);
  }

  public ParameterizedString(String value, Parameters parameters) {
    this.value = value;
    this.parameters = parameters;
  }

  @Override
  public ParameterizedString withParameters(Parameters parameters) {
    return new ParameterizedString(this.value, parameters);
  }

}
