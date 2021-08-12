package io.rtcore.sip.message.parsers.core;

import io.rtcore.sip.message.parsers.api.ValueListener;

public class ValueHolder<T> implements ValueListener<T> {

  private T value;

  public static <X> ValueHolder<X> create() {
    return new ValueHolder<X>();
  }

  @Override
  public void set(final T value) {
    this.value = value;
  }

  public T value() {
    return this.value;
  }

}
