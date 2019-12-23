package io.rtcore.sip.signaling.call;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import io.rtcore.sip.signaling.call.ImmutableStep;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Event;

public class SignalRecorder {

  @Value.Immutable
  interface Step {

    @Value.Parameter
    @Nullable
    Step previous();

    @Value.Parameter
    Event event();

    @Value.Parameter
    SignalState nextState();

  }

  private final Step parent;
  private final SignalState currentState;

  public SignalRecorder(Step parent, SignalState state) {
    this.parent = parent;
    this.currentState = state;
  }

  public SignalRecorder(SignalState state) {
    this(null, state);
  }

  public SignalState currentState() {
    return currentState;
  }

  public static SignalRecorder startingWith(SignalState state) {
    return new SignalRecorder(state);
  }

  public SignalRecorder apply(Event event) {
    SignalState nextState = SignalState.apply(event, this.currentState);
    return new SignalRecorder(ImmutableStep.of(this.parent, event, nextState), nextState);
  }

  public void reply(Consumer<Step> steps) {
    replayTo(this.parent, steps);
  }

  private static void replayTo(Step step, Consumer<Step> player) {

    if (step.previous() != null) {
      replayTo(step.previous(), player);
    }

    player.accept(step);

  }

}
