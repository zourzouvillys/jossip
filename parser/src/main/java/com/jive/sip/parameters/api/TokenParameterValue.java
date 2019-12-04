package com.jive.sip.parameters.api;

import com.google.common.base.Preconditions;
import com.jive.sip.base.api.Token;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class TokenParameterValue extends ParameterValue<Token>
{
  
  private Token value;

  public TokenParameterValue(Token value)
  {
    this.value = Preconditions.checkNotNull(value);
  }
  
  public TokenParameterValue(String value)
  {
    this.value = Token.from(value);
  }
  
  public TokenParameterValue(long value)
  {
    this.value = Token.from(Long.toString(value));
  }
  
  @Override
  public
  <T> T apply(ParameterValueVisitor<T> visitor)
  {
    return visitor.visit(this);
  }
  
  @Override
  public String toString()
  {
    return value.toString();
  }

}
