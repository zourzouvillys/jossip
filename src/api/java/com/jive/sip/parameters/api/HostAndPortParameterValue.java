package com.jive.sip.parameters.api;

import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper=false)
public class HostAndPortParameterValue extends ParameterValue<HostAndPort>
{
  
  private HostAndPort value;

  public HostAndPortParameterValue(String value)
  {
    Preconditions.checkNotNull(value);
    this.value = HostAndPort.fromString(value);
  }
  
  public HostAndPortParameterValue(HostAndPort value)
  {
    this.value = value;
  }
  
  @Override
  public <T> T apply(ParameterValueVisitor<T> visitor)
  {
    return visitor.visit(this);
  }
  
  @Override
  public String toString()
  {
    return value.toString();
  }
}
