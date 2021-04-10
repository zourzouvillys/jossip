package io.rtcore.sip.sigcore;

import java.util.List;

public interface StateFunction {

  /**
   * invoke this function
   * 
   * @param target
   *          the target of this invocation.
   * 
   * @param state
   *          the current state for this function.
   * 
   * @param invocations
   *          the invocation.
   * 
   */

  InvocationResponse invoke(Address target, List<PersistedValue> state, List<ToInvocation> invocations);

}
