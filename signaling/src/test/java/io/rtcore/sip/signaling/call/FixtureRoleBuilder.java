package io.rtcore.sip.signaling.call;

import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.signaling.call.SignalQueries;
import io.rtcore.sip.signaling.call.SignalState;
import io.rtcore.sip.signaling.call.TransactionState;
import io.rtcore.sip.signaling.call.UaRole;

public class FixtureRoleBuilder {

  private UaRole role;
  private SignalState currentState;

  public FixtureRoleBuilder(UaRole role, SignalState currentState) {
    this.role = role;
    this.currentState = currentState;
  }

  public TransactionState txn(UaRole role, long sequence) {
    return SignalQueries.transaction(currentState, role, sequence);
  }

  private long nextSequence() {
    return SignalQueries.lastSequence(this.currentState, role).orElse(0) + 1;
  }

  public FixtureRequestBuilder invite() {
    return invite(nextSequence());
  }

  public FixtureRequestBuilder invite(long sequence) {
    return new FixtureRequestBuilder(this.currentState, ImmutableSequence.of(this.role, sequence, SipMethod.INVITE));
  }

  public FixtureRequestBuilder ack(long sequence) {
    return new FixtureRequestBuilder(this.currentState, ImmutableSequence.of(this.role, sequence, SipMethod.ACK));
  }

  public FixtureRequestBuilder ack() {
    return ack(SignalQueries.latestTransaction(this.currentState, this.role, SipMethod.INVITE).getKey().longValue());
  }

  public FixtureRequestBuilder prack() {
    return prack(nextSequence());
  }

  public FixtureRequestBuilder prack(long sequence) {
    return new FixtureRequestBuilder(this.currentState, ImmutableSequence.of(this.role, sequence, SipMethod.PRACK));
  }

  public FixtureRequestBuilder bye() {
    return bye(nextSequence());
  }

  public FixtureRequestBuilder bye(long sequence) {
    return new FixtureRequestBuilder(this.currentState, ImmutableSequence.of(this.role, sequence, SipMethod.BYE));
  }

  public FixtureRequestBuilder update() {
    return update(nextSequence());
  }

  public FixtureRequestBuilder update(long sequence) {
    return new FixtureRequestBuilder(this.currentState, ImmutableSequence.of(this.role, sequence, SipMethod.UPDATE));
  }

  /////

  public FixtureResponseBuilder trying() {
    return trying(SignalQueries.latestTransaction(this.currentState, this.role.swap(), SipMethod.INVITE).getKey().longValue());
  }

  public FixtureResponseBuilder trying(long sequence) {
    return new FixtureResponseBuilder(this.currentState, ImmutableSequence.of(this.role.swap(), sequence, SipMethod.INVITE), 100);
  }

  public FixtureResponseBuilder ringing() {
    return ringing(SignalQueries.latestTransaction(this.currentState, this.role.swap(), SipMethod.INVITE).getKey().longValue());
  }

  public FixtureResponseBuilder ringing(long sequence) {
    return new FixtureResponseBuilder(this.currentState, ImmutableSequence.of(this.role.swap(), sequence, SipMethod.INVITE), 180);
  }

  public FixtureResponseBuilder sessionProgress() {
    return sessionProgress(SignalQueries.latestTransaction(this.currentState, this.role.swap(), SipMethod.INVITE).getKey().longValue());
  }

  public FixtureResponseBuilder sessionProgress(long sequence) {
    return new FixtureResponseBuilder(this.currentState, ImmutableSequence.of(this.role.swap(), sequence, SipMethod.INVITE), 183);
  }

  public FixtureResponseBuilder ok(SipMethod method) {
    return ok(SignalQueries.latestTransaction(this.currentState, this.role.swap(), method).getKey().longValue());
  }

  public FixtureResponseBuilder ok(long sequence) {
    return new FixtureResponseBuilder(this.currentState, ImmutableSequence.of(this.role.swap(), sequence, txn(this.role.swap(), sequence).method()), 200);
  }

  public FixtureResponseBuilder respond(SipMethod method, int statusCode) {
    return ok(SignalQueries.latestTransaction(this.currentState, this.role.swap(), method).getKey().longValue());
  }

  public FixtureResponseBuilder respond(long sequence, SipMethod method, int statusCode) {
    return new FixtureResponseBuilder(
      this.currentState,
      ImmutableSequence.of(this.role.swap(), sequence, txn(this.role.swap(), sequence).method()),
      statusCode);
  }

}
