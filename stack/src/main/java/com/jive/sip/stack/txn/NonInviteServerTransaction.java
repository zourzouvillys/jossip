package com.jive.sip.stack.txn;

import java.time.Duration;

import com.google.common.base.Preconditions;
import com.jive.sip.message.api.BranchId;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.api.FlowId;
import com.jive.sip.transport.api.SipRequestReceivedEvent;

/**
 * Domain model for a single transaction.
 *
 * @author theo
 */

public final class NonInviteServerTransaction implements ServerTransaction, NonInviteTransaction
{

  private SipResponse response;
  private FlowId flowId;

  public static enum State
  {
    Initial,
    Trying,
    Proceeding,
    Completed,
    Terminated
  }

  /**
   *
   */

  private State state = State.Initial;
  private final BranchId branchId;

  /**
   *
   */

  public NonInviteServerTransaction(final BranchId branchId)
  {
    this.branchId = branchId;
  }

  /**
   *
   * @param newState
   */

  private void setState(final State newState)
  {
    this.state = newState;
  }

  /**
   *
   */

  @Override
  public void fromNetwork(final SipRequestReceivedEvent e, final TransactionListener listener)
  {

    switch (this.state)
    {
      case Initial:
        // pass the message on to the app layer.
        this.flowId = e.getFlowId();
        this.setState(State.Trying);
        listener.sendToApp(null, e.getMessage());
        break;
      case Trying:
        break;
      case Proceeding:
      case Completed:
        // always send the response down the same flow as we received it.
        Preconditions.checkNotNull(this.response);
        listener.sendToNetwork(e.getFlowId(), this.response);
        break;
      case Terminated:
        break;
    }

  }

  private BranchId getBranchId()
  {
    return this.branchId;
  }

  @Override
  public void fromApp(final SipResponse res, final TransactionListener listener)
  {

    Preconditions.checkNotNull(res);

    if ((res.getStatus().getCode() / 100) == 1)
    {
      switch (this.state)
      {
        case Trying:
          this.response = res;
          this.setState(State.Proceeding);
          listener.sendToNetwork(this.flowId, this.response);
          break;
        case Proceeding:
          this.response = res;
          listener.sendToNetwork(this.flowId, this.response);
          break;
        case Initial:
        case Completed:
        case Terminated:
          break;
      }
    }
    else if ((res.getStatus().getCode() / 100) >= 2)
    {

      // schedule the expiry timer.
      // TODO change to dynamic value.
      listener.schedule(this.getBranchId(), Duration.ofSeconds(32));

      switch (this.state)
      {
        case Trying:
          this.response = res;
          this.setState(State.Completed);
          listener.sendToNetwork(this.flowId, this.response);
          break;
        case Proceeding:
          this.response = res;
          this.setState(State.Completed);
          listener.sendToNetwork(this.flowId, this.response);
          break;
        case Initial:
        case Completed:
        case Terminated:
          break;
      }

    }

  }

  public State getState()
  {
    return this.state;
  }

}
