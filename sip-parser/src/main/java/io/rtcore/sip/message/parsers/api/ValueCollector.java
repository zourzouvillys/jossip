package io.rtcore.sip.message.parsers.api;

public interface ValueCollector<T, R> {

  void collect(final T value);

  R value();

}
