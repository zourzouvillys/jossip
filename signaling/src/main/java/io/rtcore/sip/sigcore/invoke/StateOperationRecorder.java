package io.rtcore.sip.sigcore.invoke;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.rtcore.sip.sigcore.Address;
import io.rtcore.sip.sigcore.ExpirationSpec.ExpireMode;

public class StateOperationRecorder implements StateHandle {

  private Map<String, ? super Object> state = new HashMap<>();
  private final List<StateOperation> out;

  public StateOperationRecorder(List<StateOperation> out) {
    this.out = out;
  }

  @Override
  public <T> void invoke(Address target, T body) {
    out.add(ImmutableStateOperation.Invoke.of(null, target, body));
  }

  @Override
  public <T> void mergeState(String stateName, T state) {
    this.state.put(stateName, state);
    out.add(ImmutableStateOperation.MergeState.of(stateName, state, ExpireMode.NONE));
  }

  @Override
  public <T> T getState(String stateName, Class<T> stateType) {
    return stateType.cast(state.get(stateName));
  }

}
