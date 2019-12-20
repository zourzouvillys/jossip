package io.rtcore.sip.message.message.api;

import java.time.Duration;

public final class ActiveSubscriptionState implements SubscriptionState {
  private final Duration expires;

  public String toString() {
    return String.format("active;expires=%d", expires.getSeconds());
  }

  public ActiveSubscriptionState(final Duration expires) {
    this.expires = expires;
  }

  public Duration expires() {
    return this.expires;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof ActiveSubscriptionState)) return false;
    final ActiveSubscriptionState other = (ActiveSubscriptionState) o;
    final Object this$expires = this.expires();
    final Object other$expires = other.expires();
    if (this$expires == null ? other$expires != null : !this$expires.equals(other$expires)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $expires = this.expires();
    result = result * PRIME + ($expires == null ? 43 : $expires.hashCode());
    return result;
  }
}
