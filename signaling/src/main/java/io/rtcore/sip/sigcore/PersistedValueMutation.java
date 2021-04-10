package io.rtcore.sip.sigcore;

public interface PersistedValueMutation {

  enum MutationType {
    DELETE,
    MODIFY,
  }

  MutationType mutationType();

  String stateName();

  TypedValue stateValue();

}
