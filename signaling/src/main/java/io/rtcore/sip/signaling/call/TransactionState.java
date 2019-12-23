package io.rtcore.sip.signaling.call;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

import com.google.common.base.Verify;
import com.google.common.primitives.UnsignedInteger;

import io.rtcore.sip.iana.SipStatusCategory;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Event;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.ReliableAck;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.RequestEvent;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.ResponseEvent;

@Value.Immutable
@Value.Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE,
    overshadowImplementation = true,
    defaultAsDefault = true)
public abstract class TransactionState implements WithTransactionState, Retransmittable {

  /**
   * millis since txn epoch that we saw the request.
   */

  public abstract long startedAt();

  /**
   * the method of the request. note that this will not include ACK.
   */

  public abstract SipMethod method();

  /**
   * the most recent response code.
   */

  public abstract Optional<ReliableResponse> lastStatus();

  /**
   * the most recent RSeq, if any.
   */

  public abstract OptionalLong lastReliableSequence();

  /**
   * each unacknowledged reliable provisional response.
   */

  public abstract Map<UnsignedInteger, ReliableResponse> reliableResponses();

  @Value.Immutable
  public interface ReliableResponse extends Retransmittable {

    @Value.Parameter
    int code();

    @Value.Parameter
    long startedAt();

    @Override
    default long nextTransmission() {
      return startedAt() + 50;
    }

  }

  @Override
  public long nextTransmission() {
    return startedAt() + 50;
  }

  public static TransactionState initialState(RequestEvent e) {
    return ImmutableTransactionState.builder()
      .startedAt(e.millisSinceTxnEpoch())
      .method(e.seq().method())
      .build();
  }

  public static TransactionState apply(Event e, SignalState session, TransactionState state) {

    if (e instanceof ResponseEvent) {

      ResponseEvent res = (ResponseEvent) e;

      if (state.method().isInvite()) {

        switch (SipStatusCategory.forCode(res.status())) {

          case PROVISIONAL: {
            HashMap<UnsignedInteger, ReliableResponse> pending = new HashMap<>(state.reliableResponses());
            res.reliableSequence().ifPresent(seq -> {
              pending.put(UnsignedInteger.valueOf(seq), ImmutableReliableResponse.of(res.status(), res.millisSinceTxnEpoch()));
            });

            if (res.reliableSequence().isPresent()) {
              state = state.withLastReliableSequence(OptionalLong.of(res.reliableSequence().getAsLong()));
            }

            return state
              .withLastStatus(ImmutableReliableResponse.of(res.status(), res.millisSinceTxnEpoch()))
              .withReliableResponses(pending);
          }

          case SUCCESSFUL:
            // need to wait for ack.
            return state
              .withLastStatus(ImmutableReliableResponse.of(res.status(), res.millisSinceTxnEpoch()));

          case REDIRECTION:
          case REQUEST_FAILURE:
          case SERVER_FAILURE:
          case GLOBAL_FAILURE:
            return null;
          default:
            throw new IllegalArgumentException();
        }

      }
      else {

        if (res.status() >= 200) {
          // final, remove.
          return null;
        }

      }

      return state
        .withLastStatus(ImmutableReliableResponse.of(res.status(), res.millisSinceTxnEpoch()));

    }
    else if (e.seq().method().isAck()) {

      // well good, this means the txn is complete.
      return null;

    }
    else {
      throw new IllegalArgumentException(e.toString());
    }

  }

  public static TransactionState prack(ReliableAck rack, SignalState session, TransactionState state) {

    Verify.verifyNotNull(state);

    HashMap<UnsignedInteger, ReliableResponse> pending = new HashMap<>(state.reliableResponses());

    ReliableResponse key = pending.remove(UnsignedInteger.valueOf(rack.reliableSequence()));

    if (key == null) {
      throw new IllegalArgumentException(String.format("unexpected reliable sequence %s, expecting %s", rack.reliableSequence(), pending.keySet()));
    }

    return state.withReliableResponses(pending);

  }

}
