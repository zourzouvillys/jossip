package com.jive.sip.parameters.api;


import com.google.common.base.Strings;
import com.jive.sip.base.api.Token;

import lombok.Value;

@Value
public class RawParameter
{

  Token name;
  ParameterValue<?> value;

  /**
   * A new RawParameter flag (without a value).
   * 
   * @param name
   */
  
  public RawParameter(CharSequence name)
  {
    this.name = Token.from(name);
    this.value = FlagParameterValue.getInstance();
  }

  public RawParameter(Token name, ParameterValue<?> value)
  {
    this.name = name;
    this.value = value;
  }


  public RawParameter(CharSequence name, ParameterValue<?> value)
  {
    this.name = Token.from(name);
    this.value = value;
  }

  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    sb.append(name);
    if (!Strings.isNullOrEmpty(this.value.toString()))
    {
      sb.append('=').append(value);
    }
    return sb.toString();
  }

}
