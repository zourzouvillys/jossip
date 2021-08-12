package io.rtcore.sip.message.message.api;

import java.time.Duration;

public final class PendingSubscriptionState implements SubscriptionState {
  private final Duration expires;

  public PendingSubscriptionState() {
    this.expires = null;
  }

  public PendingSubscriptionState(Duration expires) {
    this.expires = expires;
  }

  public String toString() {
    if (this.expires != null) {
      return String.format("pending;expires=%d", expires.getSeconds());
    }
    return "pending";
  }

  public Duration expires() {
    return this.expires;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof PendingSubscriptionState)) return false;
    final PendingSubscriptionState other = (PendingSubscriptionState) o;
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
