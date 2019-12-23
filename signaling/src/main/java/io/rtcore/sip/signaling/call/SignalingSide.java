package io.rtcore.sip.signaling.call;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalLong;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Event;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.ReliableAck;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.RequestEvent;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.ResponseEvent;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Sequence;

@Value.Immutable
@Value.Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE,
    overshadowImplementation = true,
    defaultAsDefault = true)
public abstract class SignalingSide implements WithSignalingSide {

  public abstract OptionalLong lastSequence();

  public abstract Map<Long, TransactionState> transactions();

  public static SignalingSide initialState() {
    return ImmutableSignalingSide.builder().build();
  }

  /**
   * 
   */

  public static SignalingSide apply(Event e, SignalState session, SignalingSide side) {

    HashMap<Long, TransactionState> txns = new HashMap<>(side.transactions());

    TransactionState existing = txns.get(e.seq().sequence());

    if (existing == null) {

      long sequence = e.seq().sequence();

      // ensure that we reject new request with sequence older than the latest seen sequence.
      if (side.lastSequence().isPresent()) {
        if (sequence <= side.lastSequence().getAsLong()) {
          throw new IllegalArgumentException(String.format("out of order, %d unexpected", side.lastSequence().getAsLong()));
        }
      }

      if (e.seq().method().isInvite()) {

        if (e instanceof RequestEvent) {

          switch (e.sessionDescriptionType()) {
            case NONE:
              break;
            case OFFER:
              break;
            case ANSWER:
            case PRANSWER:
            default:
              throw new IllegalArgumentException();
          }

        }

      }
      else if (e.seq().method().isPrack()) {

        // find the original.
        RequestEvent req = (RequestEvent) e;
        ReliableAck rack = req.rack().get();
        Sequence original = rack.originalSequence();

        TransactionState origtxn = txns.get(original.sequence());

        if (origtxn == null) {
          throw new IllegalArgumentException("PRACK for unknown reliable sequence");
        }

        txns.computeIfPresent(
          original.sequence(),
          (key, _existing) -> TransactionState.prack(rack, session, _existing));

      }

      txns.put(sequence, TransactionState.initialState((RequestEvent) e));

      return side
        .withLastSequence(sequence)
        .withTransactions(txns);

    }
    else {

      // ACK to INVITE or a response.

      txns.computeIfPresent(e.seq().sequence(), (key, _existing) -> TransactionState.apply(e, session, _existing));

      return side.withTransactions(txns);

    }

  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("\n");
    transactions().forEach((seq, txn) -> {
      sb.append(" ").append(seq).append(": ").append(txn).append("\n");
    });
    return sb.toString();
  }

  // ----

  /**
   * handle sending of a request.
   */

  public static SignalingSide applySend(RequestEvent e, SignalState state, SignalingSide local) {

    //

    return apply(e, state, local);

  }

  /**
   * handle sending of a response.
   */

  public static SignalingSide applySend(ResponseEvent e, SignalState state, SignalingSide local, SignalingSide remote) {

    return local;
  }

  /**
   * handle receiving a request.
   */

  public static SignalingSide applyReceive(RequestEvent e, SignalState state, SignalingSide local, SignalingSide remote) {

    return local;

  }

  /**
   * handle receiving a response.
   */

  public static SignalingSide applyReceive(ResponseEvent e, SignalState state, SignalingSide local, SignalingSide remote) {

    return apply(e, state, local);

  }

}
