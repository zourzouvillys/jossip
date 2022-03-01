package io.rtcore.sip.common;

public interface Parameters {

  // the empty parameters.
  final Parameters EMPTY = new Parameters() {};

  static Parameters of() {
    return EMPTY;
  }

}
