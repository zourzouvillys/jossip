package io.rtcore.sip.signaling.call;

public class AbstractFixtureContext<T extends AbstractFixtureContext<T>> {

  protected final SignalState currentState;

  public AbstractFixtureContext(SignalState currentState) {
    this.currentState = currentState;
  }

  @SuppressWarnings("unchecked")
  public T dumpState() {

    // System.err.println();
    // System.err.println(SignalUtil.toString(state));
    //
    // for (Event e : events) {
    // System.err.println();
    // System.err.println("---> " + e);
    // System.err.println();
    // state = SignalState.apply(e, state);
    // System.err.println(SignalUtil.toString(state));
    //
    // System.err.println();
    // System.err.println();
    //
    //
    // System.err.println();
    // System.err.println();
    //
    // }

    System.err.println(SignalUtil.toString(currentState));

    SignalQueries.pendingRetransmissions(currentState, UaRole.UAC)
      .forEach(rt -> System.err.println(
        String.format("[UAC] at %d: %s",
          rt.nextTransmission(),
          rt)));

    SignalQueries.pendingRetransmissions(currentState, UaRole.UAS)
      .forEach(rt -> System.err.println(
        String.format("[UAS] at %d: %s",
          rt.nextTransmission(),
          rt)));

    return (T) this;
  }

}
