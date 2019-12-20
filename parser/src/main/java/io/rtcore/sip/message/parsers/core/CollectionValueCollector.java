package io.rtcore.sip.message.parsers.core;

import java.util.List;

import com.google.common.collect.Lists;

import io.rtcore.sip.message.parsers.api.ValueCollector;

public class CollectionValueCollector<T> implements ValueCollector<T, List<T>> {

  List<T> values = Lists.newLinkedList();

  @Override
  public void collect(final T value) {
    this.values.add(value);
  }

  @Override
  public List<T> value() {
    return this.values;
  }

}
