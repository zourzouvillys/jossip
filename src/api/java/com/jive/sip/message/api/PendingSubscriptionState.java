package com.jive.sip.message.api;

import java.time.Duration;

import lombok.Value;

@Value
public class PendingSubscriptionState implements SubscriptionState
{

  private Duration expires;

  public PendingSubscriptionState()
  {
    this.expires = null;
  }

  public PendingSubscriptionState(Duration expires)
  {
    this.expires = expires;
  }

  public String toString()
  {
    if (this.expires != null)
    {
      return String.format("pending;expires=%d", expires.getSeconds());
    }
    return "pending";
  }

}
