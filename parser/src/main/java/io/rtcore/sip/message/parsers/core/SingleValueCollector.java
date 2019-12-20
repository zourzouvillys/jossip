package io.rtcore.sip.message.parsers.core;

import io.rtcore.sip.message.parsers.api.ValueCollector;

public class SingleValueCollector<T> implements ValueCollector<T, T> {

  private T value = null;

  @Override
  public void collect(final T value) {
    this.value = value;
  }

  @Override
  public T value() {
    return this.value;
  }

}
