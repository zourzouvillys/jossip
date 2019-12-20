package com.jive.sip.message.api;

public final class TerminatedSubscriptionState implements SubscriptionState {
  public String toString() {
    return String.format("terminated");
  }

  public TerminatedSubscriptionState() {
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof TerminatedSubscriptionState)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int result = 1;
    return result;
  }
}
