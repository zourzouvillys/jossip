package io.rtcore.sip.signaling.call;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableSet;
import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.signaling.call.OfferAnswerType;
import io.rtcore.sip.signaling.call.TransactionState.ReliableResponse;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Event;

public class FixtureResponseBuilder implements FixtureEventSupplier {

  private ImmutableResponseEvent.Builder b;
  private SignalState currentState;
  private ImmutableSequence seq;

  public FixtureResponseBuilder(SignalState currentState, ImmutableSequence seq, int status) {
    this.currentState = currentState;
    this.seq = seq;
    this.b = ImmutableResponseEvent.builder();
    this.b.seq(seq);
    this.b.millisSinceTxnEpoch(0);
    this.b.status(status);
  }

  public FixtureResponseBuilder rseq() {

    SignalingSide side = this.currentState.side(this.seq.initiator());

    TransactionState txn = side.transactions().get(this.seq.sequence());

    long nextRseq = txn.lastReliableSequence().orElse(0) + 1;

    return rseq(nextRseq);

  }

  public FixtureResponseBuilder rseq(long reliableSequence) {
    b.reliableSequence(reliableSequence);
    return this;
  }

  public FixtureResponseBuilder body(OfferAnswerType bodyType) {
    b.sessionDescriptionType(bodyType);
    return this;
  }

  @Override
  public List<Event> fetch() {
    return Arrays.asList(b.build());
  }

  public FixtureResponseBuilder withOffer() {
    return body(OfferAnswerType.OFFER);
  }

  public FixtureResponseBuilder withAnswer() {
    return body(OfferAnswerType.ANSWER);
  }

  public FixtureResponseBuilder withAnswerPreview() {
    return body(OfferAnswerType.PRANSWER);
  }

}
