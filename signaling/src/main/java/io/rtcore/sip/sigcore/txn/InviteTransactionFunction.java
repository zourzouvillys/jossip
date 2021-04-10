package io.rtcore.sip.sigcore.txn;

import org.immutables.value.Value;

import io.rtcore.sip.sigcore.txn.StateMachine.Event;
import io.rtcore.sip.sigcore.txn.TransactionMessages.ProvisionalResponse;
import io.rtcore.sip.sigcore.txn.TransactionMessages.RejectResponse;
import io.rtcore.sip.sigcore.txn.TransactionMessages.StartTransaction;
import io.rtcore.sip.sigcore.txn.TransactionMessages.SuccessResponse;
import io.rtcore.sip.sigcore.txn.TransactionMessages.TransportError;

public class InviteTransactionFunction {

  enum Timer {
    TimerA,
    TimerB
  }

  public static class TimerA implements Event<Timer> {
    @Override
    public Timer payload() {
      return Timer.TimerA;
    }
  }

  public static class TimerB implements Event<Object> {
    @Override
    public Timer payload() {
      return Timer.TimerB;
    }
  }

  // timer D and timer M are both just for absorbing. retransmits. instead
  // of using timers for this, we just set the expiry on the 'state' field which
  // will make us just go away after some period of time without notifiying. no external
  // notification is needed, so TTL just takes care of it.

  // ---

  @Value.Immutable
  public interface PersistedState extends WithPersistedState {

    // the current state.
    State state();

    // the request which is being sent.
    String request();

  }

  // ---

  // set of allowed states for this state machine.
  enum State {

    /**
     * this is an initial state, where we are awaiting indication of 1st transmission and the
     * transport it was sent on.
     *
     * without knowing anything about the transport, we can't set retries, and without knowing when
     * the first request was transmitted we can't know when to attempt retransmit, e.g because we're
     * trying to open a TLS connection.
     * 
     * because a response does not have to be sent on the same transport as the request, it is
     * possible that we can get a response before indication of transmission.
     * 
     */

    Calling,

    /**
     * got a provisional, waiting for a final response.
     */

    Proceeding,

    /**
     * handling retransmitted rejections.
     */

    Completed,

    /**
     * forwarding 2xx retransmits.
     */

    Accepted,

    /**
     * no instance is ever in this state as it is gone.
     */

    Terminated,

  }

  // ---

  public static final StateMachine<State, InviteClientTxnBehavior> machine =
    new StateMachine.Builder<State, InviteClientTxnBehavior>(State.Calling)

      .transition(State.Calling, StartTransaction.class, State.Calling, InviteClientTxnBehavior::startTransaction)
      .transition(State.Calling, TimerA.class, State.Calling, InviteClientTxnBehavior::sendRequest)
      .transition(State.Calling, TimerB.class, State.Terminated, InviteClientTxnBehavior::notifyTimeout)
      .transition(State.Calling, TransportError.class, State.Terminated, InviteClientTxnBehavior::notifyTU)
      .transition(State.Calling, ProvisionalResponse.class, State.Proceeding, InviteClientTxnBehavior::notifyTU)
      .transition(State.Calling, SuccessResponse.class, State.Accepted, InviteClientTxnBehavior::notifyTU)
      .transition(State.Calling, RejectResponse.class, State.Completed, InviteClientTxnBehavior::sendAckAndNotifyTU)
      //
      .transition(State.Proceeding, ProvisionalResponse.class, State.Proceeding, InviteClientTxnBehavior::notifyTU)
      .transition(State.Proceeding, SuccessResponse.class, State.Accepted, InviteClientTxnBehavior::notifyTU)
      .transition(State.Proceeding, RejectResponse.class, State.Completed, InviteClientTxnBehavior::sendAckAndNotifyTU)
      //
      .transition(State.Accepted, SuccessResponse.class, State.Accepted, InviteClientTxnBehavior::notifyTU)
      //
      .transition(State.Completed, RejectResponse.class, State.Completed, InviteClientTxnBehavior::sendAck)
      .transition(State.Completed, TransportError.class, State.Terminated, InviteClientTxnBehavior::notifyTU)
      //
      .build();

  public static <PayloadT, EventT extends Event<PayloadT>> State process(State currentState, EventT startTransaction, InviteClientTxnBehavior behavior) {
    
    return machine.process(currentState, startTransaction, behavior);
    
  }

}
