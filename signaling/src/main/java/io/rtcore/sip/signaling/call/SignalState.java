package io.rtcore.sip.signaling.call;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import org.immutables.value.Value;
import org.immutables.value.Value.Style.BuilderVisibility;
import org.immutables.value.Value.Style.ImplementationVisibility;

import com.google.common.collect.ImmutableSet;

import io.rtcore.sip.iana.SipStatusCategory;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Event;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.RequestEvent;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.ResponseEvent;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Sequence;

@Value.Immutable
@Value.Style(
    visibility = ImplementationVisibility.PACKAGE,
    builderVisibility = BuilderVisibility.PACKAGE,
    overshadowImplementation = true,
    defaultAsDefault = true)
public abstract class SignalState implements WithSignalState {

  @Value.Default
  public boolean rel1xx() {
    return false;
  }

  @Value.Default
  public long currentTime() {
    return -1;
  }

  @Value.Default
  public DialogState dialogState() {
    return DialogState.NONE;
  }

  @Value.Default
  public int negotiationCount() {
    return 0;
  }

  public abstract Optional<OfferAnswerState> activeNegotiation();

  @Value.Default
  public SignalingSide uac() {
    return SignalingSide.initialState();
  }

  @Value.Default
  public SignalingSide uas() {
    return SignalingSide.initialState();
  }

  public SignalingSide side(UaRole role) {
    return role == UaRole.UAC ? uac()
                              : uas();
  }

  public static OfferAnswerContext offerAnswerContext(SignalState state) {
    if (state.negotiationCount() == 0) {
      return OfferAnswerContext.Initial;
    }
    else if (state.dialogState() == DialogState.CONFIRMED) {
      return OfferAnswerContext.Established;
    }
    return OfferAnswerContext.Early;
  }

  /**
   * O advance the time to the given epoch.
   */

  public static SignalState advance(SignalState state, long millisSinceTxnEpoch) {
    if (millisSinceTxnEpoch <= state.currentTime()) {
      // nothing to do!
      return state;
    }
    return state.withCurrentTime(millisSinceTxnEpoch);
  }

  public static SignalState apply(Event e, SignalState state) {

    // move clock forward, but don't actually execute any timers until we're done applying the
    // events.
    if (e.millisSinceTxnEpoch() > state.currentTime()) {
      state = state.withCurrentTime(e.millisSinceTxnEpoch());
    }

    Sequence seq = e.seq();
    UaRole sender = SignalUtil.sender(e);

    switch (e.sessionDescriptionType()) {

      case NONE:

        break;

      case OFFER:

        SignalQueries
          .canOffer(state, sender)
          .stream()
          .filter(oat -> oat.matches(e))
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException(String.format("%s can not send an SDP offer here", sender)));

        state = state.withActiveNegotiation(OfferAnswerState.fromEvent(offerAnswerContext(state), e));

        break;

      case ANSWER:

        SignalQueries
          .answerRequired(state, sender)
          .stream()
          .filter(at -> at.matches(e))
          .findAny()
          .orElseThrow(() -> new IllegalArgumentException("SDP answer not allowed here"));

        state = state.withActiveNegotiation(Optional.empty());
        state = state.withNegotiationCount(state.negotiationCount() + 1);

        break;

      case PRANSWER:
        throw new IllegalArgumentException();

      default:
        throw new IllegalArgumentException();

    }

    switch (seq.initiator()) {

      case UAC:

        if (e instanceof RequestEvent) {

          // UAC initiated Request is sent by UAC.

          state = state.withDialogState(applyDialogState(e, state));
          state = state.withUac(SignalingSide.applySend((RequestEvent) e, state, state.uac()));
          state = state.withUas(SignalingSide.applyReceive((RequestEvent) e, state, state.uas(), state.uac()));

        }
        else {

          // UAC initiated Request is sent by UAS.

          state = state.withDialogState(applyDialogState(e, state));
          state = state.withUas(SignalingSide.applySend((ResponseEvent) e, state, state.uas(), state.uac()));
          state = state.withUac(SignalingSide.applyReceive((ResponseEvent) e, state, state.uac(), state.uas()));

        }

        return state;

      case UAS:

        if (e instanceof RequestEvent) {

          state = state.withDialogState(applyDialogState(e, state));
          state = state.withUas(SignalingSide.applySend((RequestEvent) e, state, state.uas()));
          state = state.withUac(SignalingSide.applyReceive((RequestEvent) e, state, state.uac(), state.uas()));

        }
        else {

          state = state.withDialogState(applyDialogState(e, state));
          state = state.withUac(SignalingSide.applySend((ResponseEvent) e, state, state.uac(), state.uas()));
          state = state.withUas(SignalingSide.applyReceive((ResponseEvent) e, state, state.uas(), state.uac()));

        }

        return state;

      default:

        throw new IllegalArgumentException();

    }

  }

  /**
   * calculate the new dialog state based on the incoming event.
   * 
   * @param e
   * @param state
   * @return
   */

  private static DialogState applyDialogState(Event e, SignalState state) {

    if (e.seq().method().isBye()) {
      return DialogState.TERMINATED;
    }

    if (e.seq().initiator() != UaRole.UAC) {
      return state.dialogState();
    }
    else if (!e.seq().method().isInvite()) {
      return state.dialogState();
    }

    switch (state.dialogState()) {

      case NONE:
        return DialogState.TRYING;

      case TRYING:
        if (e instanceof ResponseEvent) {
          ResponseEvent res = ((ResponseEvent) e);
          if (res.status() == 100) {
            return DialogState.PROCEEDING;
          }
          else if (SipStatusCategory.isSuccess(res.status())) {
            return DialogState.CONFIRMED;
          }
          else if ((res.status() > 100) && (res.status() < 200)) {
            return DialogState.EARLY;
          }
          else if (SipStatusCategory.isFailure(res.status())) {
            return DialogState.TERMINATED;
          }
        }
        break;
      case PROCEEDING:
        if (e instanceof ResponseEvent) {
          ResponseEvent res = ((ResponseEvent) e);
          if (SipStatusCategory.isSuccess(res.status())) {
            return DialogState.CONFIRMED;
          }
          else if ((res.status() > 100) && (res.status() < 200)) {
            return DialogState.EARLY;
          }
          else if (SipStatusCategory.isFailure(res.status())) {
            return DialogState.TERMINATED;
          }
        }
        break;
      case EARLY:
        if (e instanceof ResponseEvent) {
          ResponseEvent res = ((ResponseEvent) e);
          if (SipStatusCategory.isSuccess(res.status())) {
            return DialogState.CONFIRMED;
          }
          else if (SipStatusCategory.isFailure(res.status())) {
            return DialogState.TERMINATED;
          }
        }
        break;
      case CONFIRMED:
        break;
      case TERMINATED:
        break;
      default:
        break;

    }
    return state.dialogState();
  }

  public static SignalState initialState(boolean rel1xx) {
    return ImmutableSignalState.builder()
      .rel1xx(rel1xx)
      .build();
  }

  public static SignalState fromEvents(boolean rel1xx, Event... events) {
    SignalState state = SignalState.initialState(rel1xx);
    for (Event e : events) {
      state = SignalState.apply(e, state);
    }
    return state;
  }

  /**
   * given a current state and an event, provides the offer/answer type for an SDP body.
   * 
   * if the SIP message which generated this event does not include an SDP body, it does not need to
   * be called, and NONE would be provided.
   * 
   * this can be called before applying a locally generated event to see if some SDP should (or
   * could) be attached.
   * 
   * the caller will be unable to differentiate between signaling scenarios where a session
   * description is required or optional using this. use negotiationRequired() to check out if it is
   * required for a specific event.
   * 
   */

  public static OfferAnswerType categorizeNegotiation(SignalState currentState, Event e) {
    return OfferAnswerType.NONE;
  }

  /**
   * which side - if any - are we blocked on for SDP negotiation to get to a stable state?
   */

  public static Optional<UaRole> negotiationSideBlocking(SignalState state) {
    // if (state.uac().negotiationState() == NegotiationState.NONE) {
    // return Optional.of(UaRole.UAS);
    // }
    return Optional.empty();
  }

  /**
   * the OfferAnswerType expected to be provided by the given role based on the current state.
   * 
   * @param state
   * @param role
   * @return
   */

  public static Set<OfferAnswerType> expected(SignalState state, UaRole side) {

    if (side == UaRole.UAC) {
      if (state.uac().lastSequence().isEmpty())
        return EnumSet.of(OfferAnswerType.NONE, OfferAnswerType.OFFER);
      return EnumSet.of(OfferAnswerType.NONE);
    }
    else {
      if (state.dialogState() == DialogState.TRYING)
        return EnumSet.of(OfferAnswerType.OFFER);
      return EnumSet.of(OfferAnswerType.NONE);
    }

  }

  /**
   * methods currently allowed to be initiated by the specified role.
   */

  public static Set<SipMethod> allow(SignalState state, UaRole side) {
    if (side == UaRole.UAC) {
      if (state.uac().lastSequence().isEmpty())
        return ImmutableSet.of(SipMethod.INVITE);
      return ImmutableSet.of();
    }
    else {
      return ImmutableSet.of();
    }
  }

}
