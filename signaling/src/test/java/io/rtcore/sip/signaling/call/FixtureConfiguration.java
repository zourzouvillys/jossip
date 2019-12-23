package io.rtcore.sip.signaling.call;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import com.google.common.collect.ImmutableSet;

import io.rtcore.sip.iana.SipOptionTags;
import io.rtcore.sip.signaling.call.SignalRecorder;
import io.rtcore.sip.signaling.call.SignalState;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Event;

public class FixtureConfiguration extends AbstractFixtureContext<FixtureConfiguration> {

  private final SignalRecorder recorder;

  public FixtureConfiguration(SipOptionTags... activatedTags) {
    this(ImmutableSet.copyOf(activatedTags));
  }

  public FixtureConfiguration(Set<SipOptionTags> enabled) {
    this(SignalState.initialState(enabled.contains(SipOptionTags.$100REL)));
  }

  public FixtureConfiguration(SignalState state) {
    this(SignalRecorder.startingWith(state));
  }

  public FixtureConfiguration(SignalRecorder recorder) {
    super(recorder.currentState());
    this.recorder = recorder;
  }

  public FixtureConfiguration apply(UnaryOperator<FixtureConfiguration> setup) {
    return setup.apply(this);
  }

  public FixtureConfiguration given(Function<FixtureGivenBuilder, FixtureEventSupplier> b) {

    FixtureGivenBuilder gb = new FixtureGivenBuilder(this.currentState);

    SignalRecorder recorder = this.recorder;

    for (Event event : b.apply(gb).fetch()) {
      recorder = recorder.apply(event);
    }

    return new FixtureConfiguration(recorder);

  }

  public FixtureVerifier when(Function<FixtureWhenBuilder, FixtureEventSupplier> b) {

    FixtureWhenBuilder wb = new FixtureWhenBuilder(this.currentState);

    SignalRecorder recorder = this.recorder;

    List<Event> www = b.apply(wb).fetch();

    try {

      for (Event event : www) {
        recorder = recorder.apply(event);
      }

      return new FixtureVerifier(recorder, recorder.currentState());

    }
    catch (Exception e) {

      return new FixtureVerifier(recorder, recorder.currentState(), e);

    }

  }

  public FixtureVerifier then() {
    return new FixtureVerifier(this.recorder, this.currentState, null);
  }

  public static FixtureConfiguration create() {
    return new FixtureConfiguration();
  }

}
