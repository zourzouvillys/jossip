package com.jive.sip.message.api;

import lombok.Value;

@Value
public class TerminatedSubscriptionState implements SubscriptionState
{
  
  public String toString()
  {
    return String.format("terminated");
  }

}
