package io.rtcore.sip.sigcore;

public interface TypedValue<T> {

  String typename();

  T value();

}
