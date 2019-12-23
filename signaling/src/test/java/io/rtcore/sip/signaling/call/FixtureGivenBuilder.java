package io.rtcore.sip.signaling.call;

import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.signaling.call.SignalState;
import io.rtcore.sip.signaling.call.UaRole;

public class FixtureGivenBuilder {

  private final SignalState currentState;

  FixtureGivenBuilder(SignalState currentState) {
    this.currentState = currentState;
  }

  public FixtureRequestBuilder request(UaRole initiator, long sequence, SipMethod method) {
    return new FixtureRequestBuilder(this.currentState, ImmutableSequence.of(initiator, sequence, method));
  }

  public FixtureResponseBuilder response(UaRole initiator, int sequence, SipMethod method, int status) {
    return new FixtureResponseBuilder(this.currentState, ImmutableSequence.of(initiator, sequence, method), status);
  }

  public FixtureRoleBuilder uac() {
    return new FixtureRoleBuilder(UaRole.UAC, this.currentState);
  }

  public FixtureRoleBuilder uas() {
    return new FixtureRoleBuilder(UaRole.UAS, this.currentState);
  }

}
