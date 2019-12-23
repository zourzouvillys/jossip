package io.rtcore.sip.signaling.call;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import io.rtcore.sip.message.iana.SipMethods;
import io.rtcore.sip.message.iana.SipStatusCategory;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.signaling.call.OfferValidity.AnswerType;
import io.rtcore.sip.signaling.call.OfferValidity.OfferType;
import io.rtcore.sip.signaling.call.OfferValidity.StandardOfferType;

/**
 * API for querying a {@link SignalState} snapshot to get various properties.
 */

public class SignalQueries {

  /**
   * given a {@link SignalState}, what is the specified role's internal negotiation state?
   */

  public static NegotiationState negotiationState(SignalState state, UaRole role) {

    OfferAnswerState active = state.activeNegotiation().orElse(null);

    if (active == null) {
      if (state.negotiationCount() == 0)
        return NegotiationState.NONE;
      return NegotiationState.STABLE;
    }

    if (active.initiator() == role)
      return NegotiationState.HAVE_LOCAL_OFFER;
    else
      return NegotiationState.HAVE_REMOTE_OFFER;

  }

  /**
   * given a {@link SignalState}, does the specified role need to provide an offer before the
   * negotiation state can progress?
   */

  public static boolean offerRequired(SignalState state, UaRole role) {

    if (state.dialogState() == DialogState.NONE)
      return false;

    if ((role == UaRole.UAS) && (negotiationState(state, role) == NegotiationState.NONE))
      return true;

    if (canOffer(state, role).contains(StandardOfferType.InviteSuccess))
      return true;

    return false;

  }

  /**
   * given a {@link SignalState}, does the specified role need to provide an answer to a previous
   * offer before the negotiation state can progress?
   */

  public static Set<AnswerType> answerRequired(SignalState state, UaRole role) {
    return state
      .activeNegotiation()
      .filter(e -> e.initiator() == role.swap())
      .map(type -> type.offerType().answers())
      .orElse(ImmutableSet.of());
  }

  /**
   * given a {@link SignalState},, what offer types can the specified role use to make an offer? if
   * it is not possible for the role to make an offer right now, an empty set is returned.
   */

  public static Set<OfferType> canOffer(SignalState state, UaRole role) {
    if (state.activeNegotiation().isPresent()) {
      return ImmutableSet.of();
    }
    OfferAnswerContext context = SignalState.offerAnswerContext(state);
    return OfferValidity.offerTypes()
      .stream()
      .filter(type -> type.contexts().contains(context))
      .filter(type -> type.appliesTo(state, role))
      .collect(ImmutableSet.toImmutableSet());
  }

  /**
   * given a {@link SignalState}, can the specified role rollback a pending offer without sending a
   * "fake" answer and then sending a new offer?
   */

  public static boolean canRollback(SignalState state, UaRole role) {
    return false;
  }

  /**
   * provide a list of all of the transactions which require retransmission from the provided role
   * if no response is received.
   */

  public static Stream<Retransmittable> pendingRetransmissions(SignalState state, UaRole role) {

    //
    Stream<Retransmittable> requests =
      state.side(role)
        .transactions()
        .values()
        .stream()
        .filter(txn -> txn.method().isInvite())
        .filter(txn -> txn.lastStatus().filter(res -> SipStatusCategory.isSuccess(res.code())).isPresent())
        .map(txn -> txn.lastStatus().get());

    Stream<Retransmittable> responses =
      state.side(role.swap())
        .transactions()
        .values()
        .stream()
        .flatMap(txn -> txn.reliableResponses().values().stream());

    return Stream.concat(requests, responses);

  }

  /**
   * given a {@link SignalState}, provides the number of milliseconds since the transaction epoch
   * until the next timeout if no further activity is provided.
   * 
   * if there is no scheduled timeout and nothing will happen without an external event, then -1L
   * will be returned.
   * 
   * timers will occur for:
   * 
   * <ul>
   * <li>user-agent core retransmissions</li>
   * <li>session timers/keepalive servicing</li>
   * </ul>
   * 
   * they will not occur for "maintenance" timers which clean up state, for example to remove state
   * needed for absorption. as these will be automatically cleaned up the next time there is a state
   * change there is no need to actually load the state and dispatch into it just to clean up some
   * state which makes no external state changes.
   * 
   */

  public static OptionalLong nextTimeout(SignalState state, UaRole role) {

    Optional<Retransmittable> next =
      pendingRetransmissions(state, role)
        .sorted(Comparator.comparingLong(Retransmittable::nextTransmission))
        .findFirst();

    return next.map(e -> OptionalLong.of(e.nextTransmission()))
      .orElse(OptionalLong.empty());

  }

  public static TransactionState transaction(SignalState state, UaRole role, long sequence) {
    return state.side(role).transactions().get(sequence);
  }

  public static OptionalLong lastSequence(SignalState state, UaRole role) {
    return state.side(role).lastSequence();
  }

  public static Entry<Long, TransactionState> latestTransaction(SignalState state, UaRole initiator, SipMethod method) {
    return state.side(initiator)
      .transactions()
      .entrySet()
      .stream()
      .filter(txn -> txn.getValue().method().equals(method))
      .findFirst()
      .orElseThrow(() -> new IllegalStateException(String.format("unable to location txn initiated by %s with method %s", initiator, method)));
  }

  /**
   * if the state allows the given method to currently be sent by the specified role.
   */

  public static boolean canSend(SignalState state, UaRole role, SipMethods method) {

    switch (state.dialogState()) {
      case NONE:
        return false;
      case TRYING:
        return false;
      case PROCEEDING:
        return false;
      case EARLY:
        break;
      case CONFIRMED:
        break;
      case TERMINATED:
        return false;
      default:
        throw new IllegalArgumentException();
    }

    switch (method) {
      case ACK:
        return false;
      case CANCEL:
        return hasIncompleteInviteTransaction(state, role);
      case INFO:
        break;
      case INVITE:
        return !hasIncompleteInviteTransaction(state, role) && !hasIncompleteInviteTransaction(state, role.swap());
      case PRACK:
        return state.side(role)
          .transactions()
          .values()
          .stream()
          .filter(txn -> !txn.reliableResponses().isEmpty())
          .findAny()
          .isPresent();
      case UPDATE:

        if (SignalQueries.hasIncompleteInviteTransaction(state, role)) {
          return false;
        }
        if (SignalQueries.hasIncompleteInviteTransaction(state, role.swap())) {
          return false;
        }
        return state.activeNegotiation().isEmpty();
        
      case BYE:
        return true;
      default:
        return false;
    }

    return true;

  }

  public static boolean hasIncompleteInviteTransaction(SignalState state, UaRole role) {
    return state.side(role)
      .transactions()
      .values()
      .stream()
      .filter(txn -> txn.method().isInvite())
      .findAny()
      .isPresent();
  }

}
