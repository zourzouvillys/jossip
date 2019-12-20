package io.rtcore.sip.message.processor.rfc3261.message.impl;

import java.util.Collection;

import io.rtcore.sip.message.parsers.api.ValueListener;

public class CollectionAppender<T> implements ValueListener<T> {

  private final Collection<T> collection;

  public CollectionAppender(final Collection<T> collection) {
    this.collection = collection;
  }

  @Override
  public void set(final T value) {
    this.collection.add(value);
  }

}
