package com.jive.sip.parsers.core.terminal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.jive.sip.parsers.api.ValueListener;

public class ValueValidator<T> implements ValueListener<T> {

  private final T expected;
  private T value;

  public ValueValidator(final T expected) {
    this.expected = expected;
  }

  @Override
  public void set(final T value) {
    this.value = value;
    assertEquals(this.expected, value);
  }

  public T value() {
    return this.value;
  }

  public static <T> ValueValidator<T> expect(final T expected) {
    return new ValueValidator<T>(expected);
  }

}
