package io.rtcore.sip.signaling.call;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.PrintStream;
import java.util.function.Consumer;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.ObjectAssert;
import org.opentest4j.AssertionFailedError;

import io.rtcore.sip.signaling.call.NegotiationState;
import io.rtcore.sip.signaling.call.SignalQueries;
import io.rtcore.sip.signaling.call.SignalRecorder;
import io.rtcore.sip.signaling.call.SignalState;
import io.rtcore.sip.signaling.call.SignalUtil;
import io.rtcore.sip.signaling.call.SignalingSide;
import io.rtcore.sip.signaling.call.UaRole;

public class FixtureVerifier extends AbstractFixtureContext<FixtureVerifier> {

  private final SignalRecorder previousState;
  private final SignalState currentState;
  private final Exception exception;

  public FixtureVerifier(SignalRecorder previousState, SignalState currentState) {
    this(previousState, currentState, null);
  }

  public FixtureVerifier(SignalRecorder previousState, SignalState currentState, Exception e) {
    super(currentState);
    this.previousState = previousState;
    this.currentState = currentState;
    this.exception = e;
  }

  public FixtureVerifier expectState(Consumer<SignalState> state) {
    state.accept(this.currentState);
    return this;
  }

  public FixtureVerifier expectSide(UaRole role, Consumer<SignalingSide> state) {
    state.accept(this.currentState.side(role));
    return this;
  }

  public <T> FixtureVerifier expectThat(Function<SignalState, T> extract, Consumer<ObjectAssert<T>> state) {
    state.accept(Assertions.assertThat(extract.apply(this.currentState)));
    return this;
  }

  public FixtureVerifier expectThatState(Consumer<ObjectAssert<SignalState>> state) {
    state.accept(Assertions.assertThat(this.currentState));
    return this;
  }

  public FixtureVerifier expectStableNegotiation() {
    return expectStableNegotiation(UaRole.UAC).expectStableNegotiation(UaRole.UAS);
  }

  public FixtureVerifier expectStableNegotiation(int negotiationCount) {
    return expectStableNegotiation(UaRole.UAC)
      .expectStableNegotiation(UaRole.UAS)
      .expectThat(SignalState::negotiationCount, e -> e.isEqualTo(negotiationCount));
  }

  public FixtureVerifier expectNegotiationState(UaRole role, NegotiationState negotiationState) {
    return expectThat(state -> SignalQueries.negotiationState(state, role), a -> a.isEqualTo(negotiationState));
  }

  public FixtureVerifier expectStableNegotiation(UaRole role) {
    return expectNegotiationState(role, NegotiationState.STABLE);
  }

  public FixtureVerifier expectException() {
    if (this.exception == null) {
      dumpHistory(System.err);
    }
    assertNotNull(this.exception);
    return this;
  }

  public FixtureVerifier dumpHistory(PrintStream out) {
    out.println("---- START");
    this.previousState.reply(step -> {
      out.println();
      out.println("--[APPLY]>>> " + step.event());
      out.println();
      out.println(SignalUtil.toString(step.nextState()));
    });
    out.println();
    out.println("---- END");
    out.println();
    return this;
  }

  public FixtureVerifier expectNoExceptions() {
    if (this.exception != null) {
      throw new AssertionFailedError("expected no exceptions, but got", this.exception);
    }
    return this;
  }

}
