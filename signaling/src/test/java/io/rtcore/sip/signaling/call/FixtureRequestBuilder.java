package io.rtcore.sip.signaling.call;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.signaling.call.OfferAnswerType;
import io.rtcore.sip.signaling.call.SignalQueries;
import io.rtcore.sip.signaling.call.SignalState;
import io.rtcore.sip.signaling.call.TransactionState.ReliableResponse;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Event;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Sequence;

public class FixtureRequestBuilder implements FixtureEventSupplier {

  private ImmutableRequestEvent.Builder b;
  private Sequence seq;
  private SignalState currentState;

  public FixtureRequestBuilder(SignalState currentState, Sequence seq) {
    this.currentState = currentState;
    this.b = ImmutableRequestEvent.builder();
    this.b.seq(seq);
    this.b.millisSinceTxnEpoch(0);
    this.seq = seq;
  }

  public FixtureRequestBuilder rack(long reliableSequence, long cseq) {
    b.rack(ImmutableReliableAck.of(reliableSequence, ImmutableSequence.of(seq.initiator(), cseq, SipMethod.INVITE)));
    return this;
  }

  public FixtureRequestBuilder rack(long reliableSequence) {
    long latestInviteSequence = SignalQueries.latestTransaction(this.currentState, this.seq.initiator(), SipMethod.INVITE).getKey().longValue();
    b.rack(ImmutableReliableAck.of(reliableSequence, ImmutableSequence.of(seq.initiator(), latestInviteSequence, SipMethod.INVITE)));
    return this;
  }

  public FixtureRequestBuilder rack() {

    Entry<Long, TransactionState> txn = SignalQueries.latestTransaction(this.currentState, this.seq.initiator(), SipMethod.INVITE);

    long latestInviteSequence = txn.getKey().longValue();

    Collection<UnsignedInteger> pendingReliableSequences = txn.getValue().reliableResponses().keySet();

    if (pendingReliableSequences.size() != 1) {
      throw new IllegalArgumentException();
    }

    long reliableSequence = pendingReliableSequences.iterator().next().longValue();
    
    b.rack(ImmutableReliableAck.of(reliableSequence, ImmutableSequence.of(seq.initiator(), latestInviteSequence, SipMethod.INVITE)));

    return this;
  }

  public FixtureRequestBuilder body(OfferAnswerType bodyType) {
    b.sessionDescriptionType(bodyType);
    return this;
  }

  public FixtureRequestBuilder withOffer() {
    return body(OfferAnswerType.OFFER);
  }

  public FixtureRequestBuilder withAnswer() {
    return body(OfferAnswerType.ANSWER);
  }

  @Override
  public List<Event> fetch() {
    return Arrays.asList(b.build());
  }

}
