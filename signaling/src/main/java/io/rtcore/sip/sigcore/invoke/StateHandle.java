package io.rtcore.sip.sigcore.invoke;

import io.rtcore.sip.sigcore.Address;

public interface StateHandle {

  /**
   * request that the given message is invoked.
   */

  default void invoke(Address target) {
    invoke(target, null);
  }

  <T> void invoke(Address target, T body);

  /**
   * 
   */

  <T> void mergeState(String key, T value);

  /**
   * 
   */

  <T> T getState(String stateKey, Class<T> stateType);

}
