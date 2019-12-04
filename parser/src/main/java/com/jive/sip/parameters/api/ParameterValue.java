package com.jive.sip.parameters.api;


public abstract class ParameterValue<T>
{
  
  abstract public <R> R apply(ParameterValueVisitor<R> visitor);
 
  abstract public T getValue();
  
  abstract public int hashCode();
  
  abstract public boolean equals(Object obj);
  
}
