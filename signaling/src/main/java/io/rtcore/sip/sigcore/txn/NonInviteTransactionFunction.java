package io.rtcore.sip.sigcore.txn;

import io.rtcore.sip.sigcore.txn.StateMachine.Event;
import io.rtcore.sip.sigcore.txn.TransactionMessages.FinalResponse;
import io.rtcore.sip.sigcore.txn.TransactionMessages.ProvisionalResponse;
import io.rtcore.sip.sigcore.txn.TransactionMessages.StartTransaction;
import io.rtcore.sip.sigcore.txn.TransactionMessages.TransportError;

public class NonInviteTransactionFunction {

  // ---

  enum Timer {
    TimerE,
  }

  public static class TimerE implements Event<Timer> {
    @Override
    public Timer payload() {
      return Timer.TimerE;
    }
  }

  // ---

  enum State {

    /**
     * waiting for a response (and retransmitting while we are).
     */

    Trying,

    /**
     * got a provisional, waiting for a final response.
     */

    Proceeding,

    /**
     * absorbing retransmits.
     */

    Completed,

    /**
     * no instance is ever in this state as it is gone.
     */

    Terminated,

  }

  // ---

  public static final StateMachine<State, NonInviteClientTxnBehavior> machine =
    new StateMachine.Builder<State, NonInviteClientTxnBehavior>(State.Trying)
      //
      .transition(State.Trying, StartTransaction.class, State.Trying, NonInviteClientTxnBehavior::sendRequest)
      .transition(State.Trying, TimerE.class, State.Trying, NonInviteClientTxnBehavior::sendRequest)
      .transition(State.Trying, TransportError.class, State.Terminated, NonInviteClientTxnBehavior::notifyTU)
      .transition(State.Trying, ProvisionalResponse.class, State.Proceeding, NonInviteClientTxnBehavior::notifyTU)
      .transition(State.Trying, FinalResponse.class, State.Completed, NonInviteClientTxnBehavior::notifyTU)
      //
      .transition(State.Proceeding, TimerE.class, State.Proceeding, NonInviteClientTxnBehavior::sendRequest)
      .transition(State.Proceeding, TransportError.class, State.Terminated, NonInviteClientTxnBehavior::notifyTU)
      .transition(State.Proceeding, ProvisionalResponse.class, State.Proceeding, NonInviteClientTxnBehavior::notifyTU)
      .transition(State.Proceeding, FinalResponse.class, State.Completed, NonInviteClientTxnBehavior::notifyTU)
      //
      .build();

  public static <PayloadT, EventT extends Event<PayloadT>> State process(State currentState, EventT startTransaction, InviteClientTxnBehavior behavior) {
    return machine.process(currentState, startTransaction, behavior);
  }

}
