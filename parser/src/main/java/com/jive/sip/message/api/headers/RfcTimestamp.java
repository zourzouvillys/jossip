/**
 * 
 */
package com.jive.sip.message.api.headers;

import java.util.Optional;

import com.google.common.base.Preconditions;

/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 */
public final class RfcTimestamp {
  Integer timePartOne;
  Optional<Integer> timePartTwo;
  Optional<Integer> delayPartOne;
  Optional<Integer> delayPartTwo;

  public RfcTimestamp(Integer timePartOne, Optional<Integer> timePartTwo, Optional<Integer> delayPartOne, Optional<Integer> delayPartTwo) {
    Preconditions.checkNotNull(timePartOne);
    this.timePartOne = timePartOne;
    this.timePartTwo = timePartTwo;
    this.delayPartOne = delayPartOne;
    this.delayPartTwo = delayPartTwo;
  }

  public RfcTimestamp(Integer timePartOne, Integer timePartTwo, Integer delayPartOne, Integer delayPartTwo) {
    Preconditions.checkNotNull(timePartOne);
    this.timePartOne = timePartOne;
    this.timePartTwo = Optional.ofNullable(timePartTwo);
    this.delayPartOne = Optional.ofNullable(delayPartOne);
    this.delayPartTwo = Optional.ofNullable(delayPartTwo);
  }

  public RfcTimestamp withTimePartOne(final Integer timePartOne) {
    return this.timePartOne == timePartOne ? this : new RfcTimestamp(timePartOne, this.timePartTwo, this.delayPartOne, this.delayPartTwo);
  }

  public RfcTimestamp withTimePartTwo(final Optional<Integer> timePartTwo) {
    return this.timePartTwo == timePartTwo ? this : new RfcTimestamp(this.timePartOne, timePartTwo, this.delayPartOne, this.delayPartTwo);
  }

  public RfcTimestamp withDelayPartOne(final Optional<Integer> delayPartOne) {
    return this.delayPartOne == delayPartOne ? this : new RfcTimestamp(this.timePartOne, this.timePartTwo, delayPartOne, this.delayPartTwo);
  }

  public RfcTimestamp withDelayPartTwo(final Optional<Integer> delayPartTwo) {
    return this.delayPartTwo == delayPartTwo ? this : new RfcTimestamp(this.timePartOne, this.timePartTwo, this.delayPartOne, delayPartTwo);
  }

  public Integer timePartOne() {
    return this.timePartOne;
  }

  public Optional<Integer> timePartTwo() {
    return this.timePartTwo;
  }

  public Optional<Integer> delayPartOne() {
    return this.delayPartOne;
  }

  public Optional<Integer> delayPartTwo() {
    return this.delayPartTwo;
  }

  @Override
  public boolean equals(final Object o) {
    if (o == this) return true;
    if (!(o instanceof RfcTimestamp)) return false;
    final RfcTimestamp other = (RfcTimestamp) o;
    final Object this$timePartOne = this.timePartOne();
    final Object other$timePartOne = other.timePartOne();
    if (this$timePartOne == null ? other$timePartOne != null : !this$timePartOne.equals(other$timePartOne)) return false;
    final Object this$timePartTwo = this.timePartTwo();
    final Object other$timePartTwo = other.timePartTwo();
    if (this$timePartTwo == null ? other$timePartTwo != null : !this$timePartTwo.equals(other$timePartTwo)) return false;
    final Object this$delayPartOne = this.delayPartOne();
    final Object other$delayPartOne = other.delayPartOne();
    if (this$delayPartOne == null ? other$delayPartOne != null : !this$delayPartOne.equals(other$delayPartOne)) return false;
    final Object this$delayPartTwo = this.delayPartTwo();
    final Object other$delayPartTwo = other.delayPartTwo();
    if (this$delayPartTwo == null ? other$delayPartTwo != null : !this$delayPartTwo.equals(other$delayPartTwo)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $timePartOne = this.timePartOne();
    result = result * PRIME + ($timePartOne == null ? 43 : $timePartOne.hashCode());
    final Object $timePartTwo = this.timePartTwo();
    result = result * PRIME + ($timePartTwo == null ? 43 : $timePartTwo.hashCode());
    final Object $delayPartOne = this.delayPartOne();
    result = result * PRIME + ($delayPartOne == null ? 43 : $delayPartOne.hashCode());
    final Object $delayPartTwo = this.delayPartTwo();
    result = result * PRIME + ($delayPartTwo == null ? 43 : $delayPartTwo.hashCode());
    return result;
  }
}
