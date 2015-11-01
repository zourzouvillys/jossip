package com.jive.sip.dummer.txn;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.dummer.txn.SipTransactionErrorInfo.ErrorCode;
import com.jive.sip.message.api.BranchId;
import com.jive.sip.message.api.Reason;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.message.api.SipResponseStatus;
import com.jive.sip.message.api.Via;
import com.jive.sip.message.api.ViaProtocol;
import com.jive.sip.parameters.api.RawParameter;
import com.jive.sip.parameters.api.TokenParameterValue;
import com.jive.sip.parameters.impl.DefaultParameters;
import com.jive.sip.transport.api.FlowId;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatagramInviteClientTransaction extends AbstractDatagramTransaction implements SipClientTransaction
{

  private static enum Timer
  {
    TimerA,
    TimerC,
    TimerB,
    TimerD,
    TimerM
  }

  private static enum State
  {
    Calling,
    Proceeding,
    Completed,
    Accepted,
    Terminated
  }

  public class TimerA implements Runnable
  {

    private Duration value = DatagramInviteClientTransaction.this.ops.getTimerA();

    @Override
    public void run()
    {
      if (DatagramInviteClientTransaction.this.state == State.Calling)
      {

        log.debug("Retransmitting {} to {}", this.value, DatagramInviteClientTransaction.this.flow);

        this.value = this.value.plus(this.value);
        
        if (value.compareTo(T2) > 0)        
        // if (this.value.isLongerThan(T2))
        {
          this.value = T2;
        }

        DatagramInviteClientTransaction.this.timerA = DatagramInviteClientTransaction.this.schedule(this, this.value);

        DatagramInviteClientTransaction.this.manager.send(DatagramInviteClientTransaction.this.flow,
            DatagramInviteClientTransaction.this.req);

      }
      else
      {
        log.trace("Not retransmitting - state is {}", DatagramInviteClientTransaction.this.state);
      }
    }

  }

  public class TimerB implements Runnable
  {
    @Override
    public void run()
    {
      if (DatagramInviteClientTransaction.this.state == State.Calling)
      {
        if (DatagramInviteClientTransaction.this.listener != null)
        {
          DatagramInviteClientTransaction.this.listener.onError(new SipTransactionErrorInfo(ErrorCode.Timeout));
        }
        DatagramInviteClientTransaction.this.setState(State.Terminated);
      }
    }
  }

  /**
   * Handle "stuck" transactions where we don't receive a provisional for 3 minutes.
   */

  public class TimerC implements Runnable
  {
    @Override
    public void run()
    {
      if (DatagramInviteClientTransaction.this.state == State.Proceeding)
      {
        // trigger a CANCEL which in turn triggers request timeout if we don't get a response.
        log.warn("TimerC fired");
        DatagramInviteClientTransaction.this.cancel(Reason.fromSipStatus(SipResponseStatus.REQUEST_TIMEOUT));
      }
    }
  }

  public class TimerD implements Runnable
  {
    @Override
    public void run()
    {
      if (DatagramInviteClientTransaction.this.state == State.Completed)
      {
        DatagramInviteClientTransaction.this.setState(State.Terminated);
      }
    }
  }

  public class TimerM implements Runnable
  {
    @Override
    public void run()
    {
      if (DatagramInviteClientTransaction.this.state == State.Accepted)
      {
        DatagramInviteClientTransaction.this.setState(State.Terminated);
      }
    }
  }

  private FlowId flow;
  private final BranchId branch;
  private SipRequest req;
  private final ClientTransactionListener listener;
  private ScheduledFuture<?> timerA;
  private final Instant created = Instant.now();
  private State state;
  private ScheduledFuture<?> timerD;
  private ScheduledFuture<?> timerB;
  private ScheduledFuture<?> timerC;
  private ScheduledFuture<?> timerM;
  private Optional<Reason> cancelled;
  private final ClientTransactionOptions ops;

  public DatagramInviteClientTransaction(
      final TransactionRuntime manager,
      final BranchId branch,
      final ClientTransactionListener listener,
      final ClientTransactionOptions ops)
  {
    super(manager);
    this.ops = (ops == null) ? ClientTransactionOptions.DEFAULT : ops;
    this.branch = branch;
    this.listener = listener;
  }

  @Override
  public void fromApplication(final SipRequest req, final FlowId flowId)
  {

    Preconditions.checkState(this.state == null);

    log.debug("Using branch {}", this.branch.getValue());

    this.flow = flowId;
    this.req = TransactionUtils.addVia(req, this.branch, this.manager, ViaProtocol.UDP, flowId);

    this.setState(State.Calling);

    this.manager.send(this.flow, this.req);

  }

  /**
   * Sets the state, returning true if it resulted in a state change.
   *
   * @param state
   * @return
   */

  private boolean setState(final State state)
  {

    if (this.state == state)
    {
      return false;
    }

    log.debug("ICT: {} -> {}", this.state, state);

    final State oldState = this.state;

    this.state = state;

    if (oldState != null)
    {
      this.leaveState(oldState);
    }

    this.enterState(this.state);

    return true;

  }

  private void enterState(final State state)
  {
    switch (state)
    {
      case Calling:
        // set a timer to send again if we've not got any response.
        this.timerA = this.schedule(new TimerA(), this.ops.getTimerA());
        this.timerB = this.schedule(new TimerB(), this.ops.getTimerB());
        break;
      case Proceeding:
        if (this.cancelled != null)
        {
          this.sendCancel(this.cancelled.orNull());
        }
        if (this.timerC == null)
        {
          log.trace("Scheduling TimerC");
          this.timerC = this.schedule(new TimerC(), Duration.ofMinutes(3));
        }
        break;
      case Accepted:
        this.timerM = this.schedule(new TimerM(), Duration.ofSeconds(32));
        break;
      case Completed:
        this.timerD = this.schedule(new TimerD(), Duration.ofSeconds(32));
        break;
      case Terminated:
        this.manager.onTerminated(this);
        break;
    }
  }

  private void leaveState(final State oldState)
  {
    switch (this.state)
    {
      case Calling:
        if (this.timerA != null)
        {
          this.timerA.cancel(true);
          this.timerA = null;
        }
        if (this.timerB != null)
        {
          this.timerB.cancel(true);
          this.timerB = null;
        }
        break;
      case Proceeding:
        if (this.timerC != null)
        {
          log.trace("Cancelling TimerC");
          this.timerC.cancel(false);
          this.timerC = null;
        }
        break;
      case Accepted:
        break;
      case Completed:
        break;
      case Terminated:
        break;
    }
  }

  @Override
  public void fromNetwork(SipResponse res, final FlowId flow)
  {

    log.debug("Got response in ICT [{}]: {} {}", flow, this.state, res);

    // TODO: we should add a prettier way of doing this.
    final List<Via> vias = res.getVias();
    vias.remove(0);
    res = res.withoutHeaders("Via", "v");
    res = res.withParsed("Via", vias);

    switch (this.state)
    {
      case Calling:
      case Proceeding:
        if (res.getStatus().isSuccess())
        {
          this.setState(State.Accepted);
        }
        else if (res.getStatus().isFailure())
        {
          this.sendAck(res);
          this.setState(State.Completed);
        }
        else
        {
          if (!this.setState(State.Proceeding) && (this.timerC != null))
          {
            // reschedule TimerC.
            log.trace("Rescheduling TimerC");
            this.timerC.cancel(false);
            this.timerC = this.schedule(new TimerC(), Duration.ofMinutes(3));
          }
        }
        this.listener.onResponse(new SipTransactionResponseInfo(flow, res));
        break;
      case Accepted:
        if (!res.getStatus().isSuccess())
        {
          log.warn("Got unexpected {} in ICT accepted", res);
          return;
        }
        this.listener.onResponse(new SipTransactionResponseInfo(flow, res));
        break;
      case Completed:
        if (!res.getStatus().isFailure())
        {
          log.warn("Got unexpected {} in ICT accepted", res);
          return;
        }
        this.sendAck(res);
        break;
      case Terminated:
        break;
    }

  }

  private void sendAck(final SipResponse res)
  {

    log.debug("Sending ACK for {} to {}", res.getStatus(), this.flow);

    // we need to add our via header back on.

    SipRequest ack = (SipRequest) this.manager.createAck(res, Lists.newLinkedList(this.req.getRoute()));

    final List<RawParameter> params = Lists.newLinkedList();

    // add branch
    params.add(new RawParameter("branch", new TokenParameterValue(this.branch.getValue())));

    // add the via, which requires us to know the correct transport to send out of...
    final Via via = new Via(ViaProtocol.UDP, this.manager.getSelf(this.flow), DefaultParameters.from(params));

    ack = (SipRequest) ack.withoutHeaders("Via", "v").withHeader(new RawHeader("Via", via.toString()));

    this.manager.send(this.flow, ack);

  }

  /**
   * To cancel, we create a secondary non-INVITE transaction specifically for the CANCEL. This
   * should result in us getting a 487 Request Terminated.
   *
   * We can't send the CANCEL until we've at least received a 100 Trying from the other side, so if
   * we've not yet received one then we just sit back and wait.
   *
   */

  @Override
  public void cancel(final Reason reason)
  {
    if (this.cancelled != null)
    {
      log.info("Attemted to CANCEL an already CANCELled ICT");
      return;
    }
    log.debug("Cancelling INVITE");
    this.cancelled = Optional.fromNullable(reason);
    if (this.state == State.Proceeding)
    {
      this.sendCancel(reason);
    }
  }

  /**
   * Sends a CANCEL, and schedules a timer to force failure if we don't get a request terminated
   * within a reasonable amount of time. (30 seconds).
   *
   * If the CANCEL transaction fails, we immediately fail the transaction ourselves.
   *
   */

  private void sendCancel(final Reason reason)
  {
    this.manager.createCancelTransaction(this.flow, this.req, this.branch, reason, new ClientTransactionListener()
    {

      @Override
      public void onResponse(final SipTransactionResponseInfo res)
      {
        if (res.getResponse().getStatus().isFailure())
        {
          log.warn("Got failure to CANCEL in {}: {} - aborting txn",
              DatagramInviteClientTransaction.this.state,
              res.getResponse());
          if (DatagramInviteClientTransaction.this.listener != null)
          {
            DatagramInviteClientTransaction.this.listener.onError(new SipTransactionErrorInfo(ErrorCode.Timeout));
          }
          DatagramInviteClientTransaction.this.setState(State.Terminated);
        }
        else if (res.getResponse().getStatus().isSuccess())
        {
          // TODO: schedule timeout to abort txn if not received in 32 seconds?
        }
      }

      @Override
      public void onError(final SipTransactionErrorInfo err)
      {
        // fail txn.
        log.warn("Failed to CANCEL ICT in {}, failing", DatagramInviteClientTransaction.this.state);
        if (DatagramInviteClientTransaction.this.listener != null)
        {
          DatagramInviteClientTransaction.this.listener.onError(new SipTransactionErrorInfo(ErrorCode.Timeout));
        }
        DatagramInviteClientTransaction.this.setState(State.Terminated);
      }

    });
  }

  @Override
  public void cancel()
  {
    this.cancel(null);
  }

  @Override
  public BranchId getBranchId()
  {
    return this.branch;
  }

  @Override
  public Instant getCreationTime()
  {
    return this.created;
  }

}
