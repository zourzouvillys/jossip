package io.rtcore.sip.channels.netty.udp;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Encapsulates operations with demand (Reactive Streams).
 *
 * <p>
 * Demand is the aggregated number of elements requested by a Subscriber which is yet to be
 * delivered (fulfilled) by the Publisher.
 */

final class Demand {

  private final AtomicLong val = new AtomicLong();

  /**
   * Increases this demand by the specified positive value.
   *
   * @param n
   *          increment {@code > 0}
   *
   * @return {@code true} if prior to this operation this demand was fulfilled
   */

  public boolean increase(final long n) {
    if (n <= 0) {
      throw new IllegalArgumentException("non-positive subscription request: " + String.valueOf(n));
    }
    final long prev =
        this.val.getAndAccumulate(n,
          (p, i) -> (p + i) < 0 ? Long.MAX_VALUE
                                : p + i);
    return prev == 0;
  }

  /**
   * Increases this demand by 1 but only if it is fulfilled.
   *
   * @return true if the demand was increased, false otherwise.
   */

  public boolean increaseIfFulfilled() {
    return this.val.compareAndSet(0, 1);
  }

  /**
   * Tries to decrease this demand by the specified positive value.
   *
   * <p>
   * The actual value this demand has been decreased by might be less than {@code n}, including
   * {@code 0} (no decrease at all).
   *
   * @param n
   *          decrement {@code > 0}
   *
   * @return a value {@code m} ({@code 0 <= m <= n}) this demand has been actually decreased by
   */

  public long decreaseAndGet(final long n) {
    if (n <= 0) {
      throw new IllegalArgumentException(String.valueOf(n));
    }
    long p, d;
    do {
      d = this.val.get();
      p = Math.min(d, n);
    }
    while (!this.val.compareAndSet(d, d - p));
    return p;
  }

  /**
   * Tries to decrease this demand by {@code 1}.
   *
   * @return {@code true} iff this demand has been decreased by {@code 1}
   */

  public boolean tryDecrement() {
    return this.decreaseAndGet(1) == 1;
  }

  /**
   * @return {@code true} if there is no unfulfilled demand
   */

  public boolean isFulfilled() {
    return this.val.get() == 0;
  }

  /**
   * Resets this object to its initial state.
   */

  public void reset() {
    this.val.set(0);
  }

  /**
   * Returns the current value of this demand.
   *
   * @return the current value of this demand
   */

  public long get() {
    return this.val.get();
  }

  @Override
  public String toString() {
    return String.valueOf(this.val.get());
  }

}
