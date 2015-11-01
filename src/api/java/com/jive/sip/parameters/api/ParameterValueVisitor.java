package com.jive.sip.parameters.api;


public interface ParameterValueVisitor<T>
{
  T visit(FlagParameterValue parameter);
  
  T visit(TokenParameterValue parameter);
  
  T visit(QuotedStringParameterValue parameter);
  
  T visit(HostAndPortParameterValue parameter);
}
