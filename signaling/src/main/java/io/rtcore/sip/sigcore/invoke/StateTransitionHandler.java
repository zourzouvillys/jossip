package io.rtcore.sip.sigcore.invoke;

@FunctionalInterface
public interface StateTransitionHandler<EventT> {

  /**
   * handles the invocation.
   */

  void invoke(EventT event, StateContext ctx);

}
