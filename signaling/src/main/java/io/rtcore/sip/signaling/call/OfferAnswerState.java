package io.rtcore.sip.signaling.call;

import org.immutables.value.Value;

import io.rtcore.sip.signaling.call.ImmutableOfferAnswerState;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Event;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Sequence;
import io.rtcore.sip.signaling.call.OfferValidity.OfferType;

@Value.Immutable
public abstract class OfferAnswerState {

  /**
   * the side which provided the offer.
   */

  public abstract UaRole initiator();

  /**
   * the sequence which contained the offer.
   */

  public abstract Sequence sequence();

  /**
   * the type/category of the location of the offer. this provides info on where the answer can/must
   * be.
   */

  public abstract OfferType offerType();

  public static OfferAnswerState fromEvent(OfferAnswerContext context, Event e) {
    return ImmutableOfferAnswerState.builder()
      .initiator(SignalUtil.sender(e))
      .sequence(e.seq())
      .offerType(OfferValidity.offerType(e).filter(type -> type.contexts().contains(context)).orElseThrow())
      .build();
  }

}
