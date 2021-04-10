package io.rtcore.sip.sigcore;

import java.util.List;

public interface InvocationBatchRequest extends ToFunction {

  /**
   * the state values for this invocation.
   */

  List<PersistedValue> state();

  /**
   * the list of invocations. there may be multiple in the same call depending on scheduling.
   */

  List<ToInvocation> invocations();

}
