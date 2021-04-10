package io.rtcore.sip.sigcore.txn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;


public class StateMachine<StateT, EventT> {

  //

  public interface Event<T> {

    T payload();

  }

  @FunctionalInterface
  public interface NoArgTransitionHandler<EventT> {

    void invoke(EventT context);

  }

  @FunctionalInterface
  public interface EventTransitionHandler<EventT, E> {

    void invoke(EventT context, E event);

  }

  private static class Transition<StateT, EventT> {

    private StateT sourceState;
    private Class<?> eventType;
    private StateT targetState;
    private BiConsumer<EventT, Object> callback;

    public <PayloadT, E extends Event<PayloadT>> Transition(
        StateT sourceState,
        Class<E> eventType,
        StateT targetState,
        NoArgTransitionHandler<EventT> callback) {
      this.sourceState = sourceState;
      this.eventType = eventType;
      this.targetState = targetState;
      this.callback = (context, payload) -> {
        callback.invoke(context);
      };
    }

    public <PayloadT, E extends Event<PayloadT>> Transition(
        StateT sourceState,
        Class<E> eventType,
        StateT targetState,
        EventTransitionHandler<EventT, PayloadT> callback) {
      this.sourceState = sourceState;
      this.eventType = eventType;
      this.targetState = targetState;
      this.callback = (context, payload) -> {
        callback.invoke(context, (PayloadT) payload);
      };
    }

  }

  public static class Builder<StateT, EventT> {

    private StateT initialState;

    private List<Transition<StateT, EventT>> transitions = new ArrayList<>();

    public Builder(StateT initialState) {
      this.initialState = initialState;
    }

    public <
        PayloadT, E extends Event<PayloadT>>
        Builder<StateT, EventT> transition(
            StateT sourceState,
            Class<E> event,
            StateT targetState,
            NoArgTransitionHandler<EventT> callback) {

      transitions.add(new Transition<StateT, EventT>(sourceState, event, targetState, callback));
      return this;

    }

    public <
        PayloadT, E extends Event<PayloadT>>
        Builder<StateT, EventT> transition(
            StateT sourceState,
            Class<E> event,
            StateT targetState,
            EventTransitionHandler<EventT, PayloadT> callback) {

      transitions.add(new Transition<StateT, EventT>(sourceState, event, targetState, callback));
      return this;

    }

    public StateMachine<StateT, EventT> build() {
      return new StateMachine<StateT, EventT>(initialState, transitions);
    }

  }

  private final StateT initialState;
  private final List<Transition<StateT, EventT>> transitions;

  public StateMachine(StateT initialState, List<Transition<StateT, EventT>> transitions) {
    this.initialState = initialState;
    this.transitions = Collections.unmodifiableList(transitions);
  }

  public <PayloadT, MsgT extends Event<PayloadT>> StateT process(StateT currentState, MsgT event, EventT behavior) {
    Transition<StateT, EventT> t = findTransition(currentState, event.getClass());
    if (t == null) {
      throw new IllegalArgumentException(String.format("invalid transition from %s with %s", currentState, event));
    }
    t.callback.accept(behavior, event.payload());
    return t.targetState;
  }

  private Transition<StateT, EventT> findTransition(StateT currentState, Class<?> event) {

    for (Transition<StateT, EventT> t : this.transitions) {

      if (t.sourceState != currentState) {
        continue;
      }

      if (!t.eventType.isAssignableFrom(event)) {
        continue;
      }

      return t;

    }

    return null;

  }

}
