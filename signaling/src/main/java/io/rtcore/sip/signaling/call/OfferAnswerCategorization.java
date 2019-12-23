package io.rtcore.sip.signaling.call;

import java.util.Optional;
import java.util.OptionalLong;

import org.immutables.value.Value;

import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.RAck;
import io.rtcore.sip.message.message.api.SipMethod;

/**
 * given the mess of offer/answer exchange rules, provides categorization and indication of (1a)
 * when and (1b) how to send offers or answers, when we should reject messages, and how to interpret
 * the contents of messages within the offer/answer model.
 * 
 * @author theo
 *
 */

/* @formatter:off

UAC                   UAS
 | F1  INVITE (SDP)    | <- The offer in the offer/answer model.
 |-------------------->|
 | F2     1xx (SDP)    | <- The offer/answer exchange is not
 |<--------------------|    closed yet, but UAC acts as if it
 |                     | ^  receives the answer.
 | F3 1xx-rel (no SDP) | |<- a 1xx-rel may be sent without answer
 |<--------------------| |   SDP.
 | F4   PRACK (no SDP) | |
 |-------------------->| | The UAC must not send a new offer.
 | F5 2xx PRA (no SDP) | |
 |<--------------------| v
 |                     |
 | F6 1xx-rel (SDP)    | <- The answer in the offer/ answer model.
 |<--------------------| -
 | F7   PRACK          | | The UAC can send a new offer in a PRACK
 |-------------------->| | request to acknowledge F6.
 | F8 2xx PRA          | | After F7, the UAC and UAS can send a new
 |<--------------------| v offer in an UPDATE request.
 |                     |
 | F9 1xx-rel          | <- SDP should not be included in the
 |<--------------------|    subsequent 1xx-rel once offer/answer
 | F10  PRACK          |    has been completed.
 |-------------------->|
 | F11 2xx PRA         |
 |<--------------------|
 |                     |
 | F12 2xx INV         | <- SDP should not be included in the
 |<--------------------|    final response once offer/answer has
 | F13    ACK          |    been completed.
 |-------------------->|

   Figure 1: Example of Offer/Answer with 100rel Extension (1)    

* @formatter:on
*/

public class OfferAnswerCategorization {

  public static boolean isReliable(Event e) {
    if (e instanceof ResponseEvent) {
      ResponseEvent res = ((ResponseEvent) e);
      if (res.status() >= 200)
        return true;
      return res.reliableSequence().isPresent();
    }
    return true;
  }

  @Value.Immutable
  public static abstract class Sequence {

    /**
     * the transaction initiator. the sender of the original request.
     */

    @Value.Parameter
    public abstract UaRole initiator();

    @Value.Parameter
    public abstract long sequence();

    @Value.Parameter
    public abstract SipMethod method();

    @Override
    public String toString() {
      return String.format("%s(%s,%s)", initiator(), sequence(), method());
    }

  }

  @Value.Immutable
  public static abstract class ReliableAck {

    @Value.Parameter
    public abstract long reliableSequence();

    @Value.Parameter
    public abstract Sequence originalSequence();

    @Override
    public String toString() {
      return String.format("(%s,%s)", reliableSequence(), originalSequence());
    }

  }

  interface Event {

    /**
     * the time at which this event occurred.
     */

    long millisSinceTxnEpoch();

    Sequence seq();

    OfferAnswerType sessionDescriptionType();

    Event withSessionDescriptionType(OfferAnswerType value);

  }

  @Value.Immutable
  interface RequestEvent extends Event, WithRequestEvent {

    @Override
    @Value.Parameter
    long millisSinceTxnEpoch();

    @Override
    @Value.Parameter
    Sequence seq();

    @Value.Parameter
    Optional<ReliableAck> rack();

    @Override
    @Value.Parameter
    @Value.Default
    default OfferAnswerType sessionDescriptionType() {
      return OfferAnswerType.NONE;
    }

  }

  @Value.Immutable
  interface ResponseEvent extends Event, WithResponseEvent {

    @Override
    @Value.Parameter
    long millisSinceTxnEpoch();

    @Override
    @Value.Parameter
    Sequence seq();

    @Value.Parameter
    int status();

    @Value.Parameter
    OptionalLong reliableSequence();

    @Override
    @Value.Parameter
    @Value.Default
    default OfferAnswerType sessionDescriptionType() {
      return OfferAnswerType.NONE;
    }

  }

  public static Event create(long time, UaRole txninitiator, SipMessage msg) {
    return msg.apply(req -> create(time, txninitiator, req), res -> create(time, txninitiator, res));
  }

  public static Event create(long time, UaRole txninitiator, SipRequest req) {

    ImmutableRequestEvent.Builder rb = ImmutableRequestEvent.builder();

    rb.millisSinceTxnEpoch(time);

    rb.seq(ImmutableSequence.of(txninitiator, req.cseq().sequence().longValue(), req.method()));

    req.rack()
      .ifPresent(rack -> rb.rack(
        ImmutableReliableAck.of(
          rack.reliableSequence().longValue(),
          ImmutableSequence.of(txninitiator, rack.sequence().longValue(), rack.sequence().method()))));

    return rb.build();

  }

  public static Event create(long time, UaRole txninitiator, SipResponse res, OfferAnswerType oat) {
    ImmutableResponseEvent.Builder rb = ImmutableResponseEvent.builder();
    rb.millisSinceTxnEpoch(time);
    rb.seq(ImmutableSequence.of(txninitiator, res.cseq().sequence().longValue(), res.cseq().method()));
    rb.status(res.getStatus().code());
    rb.reliableSequence(res.getRSeq());
    rb.sessionDescriptionType(oat);
    return rb.build();
  }

  public static Event create(long time, UaRole txninitiator, CSeq cseq, int status) {
    return create(time, txninitiator, cseq, status, OfferAnswerType.NONE);
  }

  public static Event create(long time, UaRole txninitiator, CSeq cseq, int status, OfferAnswerType oat) {
    ImmutableResponseEvent.Builder rb = ImmutableResponseEvent.builder();
    rb.millisSinceTxnEpoch(time);
    rb.seq(ImmutableSequence.of(txninitiator, cseq.sequence().longValue(), cseq.method()));
    rb.status(status);
    rb.sessionDescriptionType(oat);
    return rb.build();
  }

  public static Event create(long time, UaRole txninitiator, CSeq cseq, int status, UnsignedInteger reliableSequence, OfferAnswerType oat) {
    ImmutableResponseEvent.Builder rb = ImmutableResponseEvent.builder();
    rb.millisSinceTxnEpoch(time);
    rb.seq(ImmutableSequence.of(txninitiator, cseq.sequence().longValue(), cseq.method()));
    rb.reliableSequence(reliableSequence.longValue());
    rb.status(status);
    rb.sessionDescriptionType(oat);
    return rb.build();
  }

  public static Event create(long time, UaRole txninitiator, CSeq cseq) {
    return create(time, txninitiator, cseq, OfferAnswerType.NONE);
  }

  public static Event create(long time, UaRole txninitiator, CSeq cseq, OfferAnswerType oat) {
    ImmutableRequestEvent.Builder rb = ImmutableRequestEvent.builder();
    rb.millisSinceTxnEpoch(time);
    rb.seq(ImmutableSequence.of(txninitiator, cseq.sequence().longValue(), cseq.method()));
    rb.sessionDescriptionType(oat);
    return rb.build();
  }

  public static Event create(long time, UaRole txninitiator, CSeq cseq, RAck rack) {
    return create(time, txninitiator, cseq, rack, OfferAnswerType.NONE);
  }

  public static Event create(long time, UaRole txninitiator, CSeq cseq, RAck rack, OfferAnswerType oat) {
    ImmutableRequestEvent.Builder rb = ImmutableRequestEvent.builder();
    rb.millisSinceTxnEpoch(time);
    rb.seq(ImmutableSequence.of(
      txninitiator,
      cseq.sequence().longValue(),
      cseq.method()));
    rb.rack(
      ImmutableReliableAck.of(
        rack.reliableSequence().longValue(),
        ImmutableSequence.of(txninitiator, rack.sequence().longValue(), rack.sequence().method())));
    rb.sessionDescriptionType(oat);
    return rb.build();
  }

}
