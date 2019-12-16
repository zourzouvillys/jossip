package com.jive.sip.message.api;

import java.time.Duration;

import lombok.Value;

@Value
public class ActiveSubscriptionState implements SubscriptionState {

  private final Duration expires;

  public String toString() {
    return String.format("active;expires=%d", expires.getSeconds());
  }

}
