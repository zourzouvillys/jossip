package com.jive.sip.parsers.api;

public interface ValueCollector<T, R> {

  void collect(final T value);

  R value();

}
