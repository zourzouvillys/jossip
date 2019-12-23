package io.rtcore.sip.signaling.call;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import io.rtcore.sip.iana.SipMethods;
import io.rtcore.sip.iana.SipStatusCategory;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Event;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.RequestEvent;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.ResponseEvent;
import io.rtcore.sip.signaling.call.OfferValidity.AnswerType;
import io.rtcore.sip.signaling.call.OfferValidity.OfferType;

public class SignalUtil {

  //

  public static String toString(SignalState session) {

    StringBuilder sb = new StringBuilder();
    sb.append("session {").append("\n");
    sb.append("  currentTime = ").append(session.currentTime()).append("\n");
    sb.append("  dialogState = ").append(session.dialogState()).append("\n");
    if (session.negotiationCount() > 0)
      sb.append("  negotiationCount = ").append(session.negotiationCount()).append("\n");
    session.activeNegotiation().ifPresent(negotiation -> sb.append("  activeNegotiation = ").append(negotiation).append("\n"));
    SignalState.negotiationSideBlocking(session).ifPresent(side -> sb.append("  (negotiationSideBlocking) = ").append(side).append("\n"));
    // if (!session.uac().equals(SignalingSide.initialState())) {
    sb.append("  uac {").append("\n");
    sb.append(toString(session, UaRole.UAC));
    sb.append("  }").append("\n");
    // }
    // if (!session.uas().equals(SignalingSide.initialState())) {
    sb.append("  uas {").append("\n");
    sb.append(toString(session, UaRole.UAS));
    sb.append("  }").append("\n");
    // }
    sb.append("}");
    return sb.toString();
  }

  public static String toString(SignalState state, UaRole role) {

    SignalingSide side = state.side(role);

    StringBuilder sb = new StringBuilder();
    side.lastSequence().ifPresent(seq -> sb.append("    lastSequence = ").append(seq).append("\n"));

    // sb.append(" offerAnswerContext = ").append(side.offerAnswerContext()).append("\n");
    // side.offerType().ifPresent(offerType -> sb.append(" offerType =
    // ").append(offerType).append("\n"));
    // sb.append(" (allow) = ").append(SignalState.allow(state, role)).append("\n");

    // if (side.negotiationState() != NegotiationState.STABLE) {
    // sb.append(" negotiationState = ").append(side.negotiationState()).append("\n");
    // }

    side.transactions().forEach((seq, txn) -> {
      sb.append("    [").append(seq).append("]: ").append(toString(txn)).append("\n");
    });

    SignalQueries.nextTimeout(state, role)
      .ifPresent(timeout -> sb.append("    (nextTimeout) = ").append(timeout).append("\n"));

    NegotiationState negotiationState = SignalQueries.negotiationState(state, role);

    if (negotiationState != NegotiationState.NONE)
      sb.append("    (negotiationState) = ").append(negotiationState).append("\n");

    Set<OfferType> canOffer = SignalQueries.canOffer(state, role);

    if (!canOffer.isEmpty()) {
      sb.append("    (canOffer) = ").append(SignalQueries.canOffer(state, role)).append("\n");
    }

    if (SignalQueries.offerRequired(state, role))
      sb.append("    (offerRequired)").append("\n");

    if (SignalQueries.canRollback(state, role))
      sb.append("    (canRollback)").append("\n");

    Set<AnswerType> answerRequired = SignalQueries.answerRequired(state, role);
    if (!answerRequired.isEmpty()) {
      sb.append("    (answerRequired) = ").append(answerRequired).append("\n");
    }

    Set<SipMethods> canSend =
      EnumSet.allOf(SipMethods.class)
        .stream()
        .filter(method -> SignalQueries.canSend(state, role, method))
        .collect(Collectors.toSet());

    sb.append("    (canSend) = ").append(canSend).append("\n");

    return sb.toString();
  }

  public static String toString(TransactionState txn) {

    StringBuilder sb = new StringBuilder();
    sb.append(txn.method());
    sb.append("(@").append(txn.startedAt()).append(")");
    txn.lastStatus().ifPresent(status -> sb.append("/").append(status.code()).append("(@").append(status.startedAt()).append(")"));
    txn.lastReliableSequence().ifPresent(rseq -> sb.append(", last RSeq ").append(rseq));
    if (!txn.reliableResponses().isEmpty()) {
      txn.reliableResponses().forEach((rseq, state) -> {
        sb.append("\n").append("      ");
        sb.append("- ").append(rseq).append(" = ").append(state);
      });
    }
    return sb.toString();

  }

  public static boolean isRequest(Event e) {
    return e instanceof RequestEvent;
  }

  public static UaRole sender(Event e) {
    return isRequest(e) ? e.seq().initiator()
                        : e.seq().initiator().swap();
  }

  public static boolean isResponse(Event e, SipStatusCategory category) {
    return asResponse(e).filter(res -> SipStatusCategory.forCode(res.status()).equals(category)).isPresent();
  }

  public static boolean isResponse(Event e, SipMethod method, SipStatusCategory category) {
    return asResponse(e)
      .filter(res -> res.seq().method().equals(method))
      .filter(res -> SipStatusCategory.forCode(res.status()).equals(category))
      .isPresent();
  }

  public static Optional<RequestEvent> asRequest(Event e) {
    return Optional.ofNullable(e)
      .filter(RequestEvent.class::isInstance)
      .map(RequestEvent.class::cast);
  }

  public static Optional<ResponseEvent> asResponse(Event e) {
    return Optional.ofNullable(e)
      .filter(ResponseEvent.class::isInstance)
      .map(ResponseEvent.class::cast);
  }

  public static boolean isRequest(Event e, SipMethod method) {
    return asRequest(e).filter(req -> req.seq().method().equals(method)).isPresent();
  }

  public static boolean isReliableProvisionalResponse(Event e) {
    return asResponse(e)
      .filter(res -> (res.status() > 100) && (res.status() < 200))
      .filter(res -> res.seq().method().isInvite())
      .filter(res -> res.reliableSequence().isPresent())
      .isPresent();
  }

}
