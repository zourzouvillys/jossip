/**
 * 
 */
package com.jive.sip.message.api.headers;

import java.util.Optional;

import com.google.common.base.Preconditions;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Wither;



/**
 * @author Jeff Hutchins <jhutchins@getjive.com>
 * 
 */
@Wither
@Getter
@EqualsAndHashCode
public final class RfcTimestamp
{
  Integer timePartOne;
  Optional<Integer> timePartTwo;
  Optional<Integer> delayPartOne;
  Optional<Integer> delayPartTwo;
  
  public RfcTimestamp(Integer timePartOne, Optional<Integer> timePartTwo, Optional<Integer> delayPartOne, 
      Optional<Integer> delayPartTwo)
  {
    Preconditions.checkNotNull(timePartOne);
    this.timePartOne = timePartOne;
    this.timePartTwo = timePartTwo;
    this.delayPartOne = delayPartOne;
    this.delayPartTwo = delayPartTwo;
  }
  
  public RfcTimestamp(Integer timePartOne, Integer timePartTwo, Integer delayPartOne, 
      Integer delayPartTwo)
  {
    Preconditions.checkNotNull(timePartOne);
    this.timePartOne = timePartOne;
    this.timePartTwo = Optional.ofNullable(timePartTwo);
    this.delayPartOne = Optional.ofNullable(delayPartOne);
    this.delayPartTwo = Optional.ofNullable(delayPartTwo);
  }
}
