package io.rtcore.sip.fixups;

import java.util.function.BinaryOperator;

import com.google.common.primitives.UnsignedInteger;

public class SingleHeaderFixups {

  /**
   * reducer which returns the smaller of the two values. useful for max-forwards.
   */

  public BinaryOperator<UnsignedInteger> smallerValue() {
    return (a, b) -> a.compareTo(b) < 0 ? a
                                        : b;
  }

}
