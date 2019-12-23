package io.rtcore.sip.signaling.call;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Verify;
import com.google.common.collect.ImmutableSet;

import io.rtcore.sip.message.iana.SipMethods;
import io.rtcore.sip.message.iana.SipStatusCategory;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.Event;
import io.rtcore.sip.signaling.call.OfferAnswerCategorization.ResponseEvent;

//
// See RFC 6337 - https://tools.ietf.org/html/rfc6337
//
//  @formatter:off
//
//       Offer                Answer             RFC    Ini Est Early
// -------------------------------------------------------------------
// 1. INVITE Req.          2xx INVITE Resp.     RFC 3261  Y   Y    N
// 2. 2xx INVITE Resp.     ACK Req.             RFC 3261  Y   Y    N
// 3. INVITE Req.          1xx-rel INVITE Resp. RFC 3262  Y   Y    N
// 4. 1xx-rel INVITE Resp. PRACK Req.           RFC 3262  Y   Y    N

// 5. PRACK Req.           200 PRACK Resp.      RFC 3262  N   Y    Y
// 6. UPDATE Req.          2xx UPDATE Resp.     RFC 3311  N   Y    Y
//


//    Offer                Rejection
// ------------------------------------------------------------------
// 1. INVITE Req. (*)      488 INVITE Response
// 2. 2xx INVITE Resp.     Answer in ACK Req. followed by new offer
//                      OR termination of dialog
// 3. INVITE Req.          488 INVITE Response (same as Pattern 1)
// 4. 1xx-rel INVITE Resp. Answer in PRACK Req. followed by new offer
// 5. PRACK Req. (**)      200 PRACK Resp. followed by new offer
//                      OR termination of dialog
// 6. UPDATE Req.          488 UPDATE Response


// @formatter:on

public class OfferValidity {

  interface OfferType {

    /**
     * the locations that answers can be provided to this offer.
     */

    Set<AnswerType> answers();

    /**
     * the contexts the given offer type is allowed to be used.
     */

    Set<OfferAnswerContext> contexts();

    boolean appliesTo(SignalState state, UaRole role);

    boolean matches(Event e);

  }

  interface IAnswerType {

    boolean matches(Event e);

  }

  private static final Set<OfferAnswerContext> STD1 = EnumSet.of(OfferAnswerContext.Initial, OfferAnswerContext.Established);
  private static final Set<OfferAnswerContext> STD2 = EnumSet.of(OfferAnswerContext.Established, OfferAnswerContext.Early);

  public enum StandardOfferType implements OfferType {

    InviteRequest(STD1, AnswerType.InviteSuccess, AnswerType.ReliableInviteResponse) {

      @Override
      public boolean appliesTo(SignalState state, UaRole role) {

        switch (state.dialogState()) {
          case NONE:
            return (role == UaRole.UAC);
          case TRYING:
            return false;
          case EARLY:
            return false;
          case CONFIRMED:
            return SignalQueries.canSend(state, role, SipMethods.INVITE);
          case PROCEEDING:
            return false;
          case TERMINATED:
            return false;
          default:
            throw new IllegalArgumentException();
        }
      }

      @Override
      public boolean matches(Event e) {
        return SignalUtil.isRequest(e, SipMethod.INVITE);
      }

    },

    InviteSuccess(STD1, AnswerType.AckRequest) {

      @Override
      public boolean appliesTo(SignalState state, UaRole role) {

        if (SignalQueries.hasIncompleteInviteTransaction(state, role.swap())) {
          return true;
        }

        return (role == UaRole.UAS) && (SignalQueries.negotiationState(state, role) == NegotiationState.NONE);
      }

      @Override
      public boolean matches(Event e) {
        return SignalUtil.isResponse(e, SipMethod.INVITE, SipStatusCategory.SUCCESSFUL);
      }

    },

    ReliableProvisional(STD1, AnswerType.PrackRequest) {

      @Override
      public boolean appliesTo(SignalState state, UaRole role) {
        return (role == UaRole.UAS) && (SignalQueries.negotiationState(state, role) == NegotiationState.NONE);
      }

      @Override
      public boolean matches(Event e) {
        return SignalUtil.isReliableProvisionalResponse(e);
      }

    },

    PrackRequest(STD2, AnswerType.PrackSuccess) {

      @Override
      public boolean appliesTo(SignalState state, UaRole role) {
        if (state.activeNegotiation().isPresent())
          return false;
        // only applies if we have a pending reliable ack.
        return false;
      }

      @Override
      public boolean matches(Event e) {
        return SignalUtil.isRequest(e, SipMethod.PRACK);
      }

    },

    UpdateRequest(STD2, AnswerType.UpdateSuccess) {

      @Override
      public boolean appliesTo(SignalState state, UaRole role) {

        if (!SignalQueries.canSend(state, role, SipMethods.UPDATE)) {
          return false;
        }

        if (state.activeNegotiation().isPresent())
          return false;

        return true;
      }

      @Override
      public boolean matches(Event e) {
        return SignalUtil.isRequest(e, SipMethod.UPDATE);
      }

    },
    ;

    private final ImmutableSet<AnswerType> answers;
    private final Set<OfferAnswerContext> contexts;

    StandardOfferType(Set<OfferAnswerContext> contexts, AnswerType... locations) {
      this.contexts = contexts;
      this.answers = ImmutableSet.copyOf(locations);
    }

    @Override
    public Set<AnswerType> answers() {
      return this.answers;
    }

    @Override
    public Set<OfferAnswerContext> contexts() {
      return this.contexts;
    }

  }

  public static final Set<OfferType> offerTypes() {
    return ImmutableSet.copyOf(StandardOfferType.values());
  }

  public enum AnswerType implements IAnswerType {

    InviteSuccess {

      @Override
      public boolean matches(Event e) {
        return SignalUtil.isResponse(e, SipStatusCategory.SUCCESSFUL);
      }

    },

    AckRequest {

      @Override
      public boolean matches(Event e) {
        return SignalUtil.isRequest(e, SipMethod.ACK);
      }

    },

    ReliableInviteResponse {

      @Override
      public boolean matches(Event e) {
        return SignalUtil.isReliableProvisionalResponse(e);
      }

    },

    PrackRequest {

      @Override
      public boolean matches(Event e) {
        return SignalUtil.isRequest(e, SipMethod.PRACK);
      }

    },

    PrackSuccess {

      @Override
      public boolean matches(Event e) {
        return SignalUtil.isResponse(e, SipMethod.PRACK, SipStatusCategory.SUCCESSFUL);
      }

    },

    UpdateSuccess {

      @Override
      public boolean matches(Event e) {
        return SignalUtil.isResponse(e, SipMethod.UPDATE, SipStatusCategory.SUCCESSFUL);
      }

    },;

  }

  public static Optional<OfferType> offerType(SipMethod method) {
    switch (method.getMethod()) {
      case "INVITE":
        return Optional.of(StandardOfferType.InviteRequest);
      case "PRACK":
        return Optional.of(StandardOfferType.PrackRequest);
      case "UPDATE":
        return Optional.of(StandardOfferType.UpdateRequest);
    }
    return Optional.empty();
  }

  public static Optional<OfferType> offerType(SipMethod method, int status, boolean reliable) {
    if ((status == 100) || ((status < 200) && !reliable) || (status >= 300)) {
      return Optional.empty();
    }
    switch (method.getMethod()) {
      case "INVITE":
        if (status < 200) {
          Verify.verify(reliable);
          return Optional.of(StandardOfferType.ReliableProvisional);
        }
        return Optional.of(StandardOfferType.InviteSuccess);
    }
    return Optional.empty();
  }

  public static Optional<OfferType> offerType(Event e) {
    if (e instanceof ResponseEvent) {
      ResponseEvent res = (ResponseEvent) e;
      return offerType(res.seq().method(), res.status(), res.reliableSequence().isPresent());
    }
    return offerType(e.seq().method());
  }

}
