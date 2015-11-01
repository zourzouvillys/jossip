package com.jive.sip.dummer.txn;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;

import com.google.common.base.Stopwatch;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.api.FlowId;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatagramInviteServerTransaction extends AbstractDatagramServerTransaction implements SipServerTransaction, ServerTransactionHandle
{

  public static enum State
  {
    Proceeding,
    Completed,
    Accepted,
    Confirmed,
    Terminated
  }

  private class TimerG implements Runnable
  {

    private Duration value = T1;

    @Override
    public void run()
    {

      if (DatagramInviteServerTransaction.this.state == State.Completed)
      {

        log.debug("TimerG fired.");

        // send response
        DatagramInviteServerTransaction.this.manager.send(DatagramInviteServerTransaction.this.flowId, DatagramInviteServerTransaction.this.res);

        // rearm
        this.value = this.value.plus(this.value);

        if (value.compareTo(T2) > 0)
        // if (this.value.isLongerThan(T2))
        {
          this.value = T2;
        }

        DatagramInviteServerTransaction.this.timerG = schedule(this, this.value);

      }

    }

  }

  private class TimerH implements Runnable
  {
    @Override
    public void run()
    {
      DatagramInviteServerTransaction.this.timerH = null;
      if (DatagramInviteServerTransaction.this.state == State.Completed)
      {
        setState(State.Terminated);
      }
      else
      {
        log.info("Ignoring TimerH in {}", DatagramInviteServerTransaction.this.state);
      }
    }
  }

  private class TimerI implements Runnable
  {
    @Override
    public void run()
    {
      DatagramInviteServerTransaction.this.timerI = null;
      if (DatagramInviteServerTransaction.this.state == State.Confirmed)
      {
        setState(State.Terminated);
      }
    }
  }

  private class TimerL implements Runnable
  {
    @Override
    public void run()
    {
      DatagramInviteServerTransaction.this.timerL = null;
      if (DatagramInviteServerTransaction.this.state == State.Accepted)
      {
        setState(State.Terminated);
      }
    }
  }

  private ServerTransactionListener listener;
  private final InviteServerTransactionHandler handler;
  private SipRequest cancelled = null;
  private final Stopwatch timer = Stopwatch.createUnstarted();
  private State state = null;

  private ScheduledFuture<?> timerG;
  private ScheduledFuture<?> timerH;
  private ScheduledFuture<?> timerI;
  private ScheduledFuture<?> timerL;

  public DatagramInviteServerTransaction(final TransactionRuntime runtime, final FlowId flowId, final InviteServerTransactionHandler handler)
  {
    super(runtime, flowId);
    this.handler = handler;
  }

  public void setState(final State state)
  {
    if (this.state == state)
    {
      return;
    }
    log.debug("IST: {} -> {}", this.state, state);
    final State oldState = this.state;
    this.state = state;
    if (oldState != null)
    {
      leaveState(oldState);
    }
    enterState(state);
  }

  private void enterState(final State state)
  {
    switch (this.state)
    {

      case Proceeding:
        // TODO: set a timer to send a 100 Trying if TU doesn't?
        break;

      case Accepted:
        this.timerL = schedule(new TimerL(), T1.multipliedBy(64));
        break;

      case Completed:
        this.timerG = schedule(new TimerG(), T1);
        this.timerH = schedule(new TimerH(), T1.multipliedBy(64));
        break;

      case Confirmed:
        schedule(new TimerI(), T4);
        break;
      case Terminated:
        this.manager.onTerminated(this);
        break;
    }
  }

  private void leaveState(final State oldState)
  {

    switch (oldState)
    {
      case Completed:
        if (this.timerG != null)
        {
          this.timerG.cancel(true);
          this.timerG = null;
        }
        if (this.timerH != null)
        {
          this.timerH.cancel(true);
          this.timerH = null;
        }
        break;
      case Confirmed:
        break;
      case Proceeding:
        break;
      case Accepted:
        break;
      case Terminated:
        break;
      default:
        break;

    }

  }

  @Override
  public void fromNetwork(final SipRequest req, final FlowId flow)
  {

    if (this.state == null)
    {
      log.trace("IST from network: {}", req);
      this.req = req;
      this.timer.start();
      this.flowId = flow;
      setState(State.Proceeding);
      this.handler.processRequest(this);
      return;
    }

    if (req.getMethod().equals(SipMethod.INVITE))
    {
      processInvite(req, flow);
    }
    else if (req.getMethod().equals(SipMethod.ACK))
    {
      processAck(req, flow);
    }
    else
    {
      log.warn("Weird {} in IST", req.getMethod());
    }

  }

  /**
   * 
   * @param req
   * @param flow
   */

  private void processInvite(final SipRequest invite, final FlowId flow)
  {

    switch (this.state)
    {
      case Proceeding:
      case Completed:
        if (this.res != null)
        {
          this.manager.send(this.flowId, this.res);
        }
        break;
      case Confirmed:
      case Accepted:
      case Terminated:
        log.debug("Ignoring retransmitted INVITE in IST");
        break;
    }

  }

  /**
   * 
   * @param req
   * @param flow
   */

  private void processAck(final SipRequest ack, final FlowId flow)
  {

    switch (this.state)
    {
      case Proceeding:
        break;
      case Completed:
        setState(State.Confirmed);
        break;
      case Confirmed:
        break;
      case Accepted:
        this.handler.processAck(ack, flow);
        break;
      case Terminated:
        break;
    }

  }

  @Override
  public void fromApplication(final SipResponse req)
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void respond(final SipResponse res)
  {

    switch (this.state)
    {
      case Proceeding:
        this.res = res;
        this.manager.send(this.flowId, this.res);
        if (res.getStatus().isSuccess())
        {
          setState(State.Accepted);
        }
        else if (res.getStatus().isFinal())
        {
          setState(State.Completed);
        }
        break;
      case Accepted:
        this.res = res;
        log.debug("Sending 2xx to {}", this.flowId);
        this.manager.send(this.flowId, this.res);
        break;
      case Completed:
      case Confirmed:
      case Terminated:
        log.warn("Got invalid IST response {} in state {}", res.getStatus(), this.state);
        break;
    }

  }


  @Override
  public void addListener(final ServerTransactionListener listener)
  {
    this.listener = listener;
    if (this.cancelled != null)
    {
      // TODO: dispatch rather than immediate?
      this.listener.onCancelled(this.cancelled);
    }
  }

  public void cancel(final SipRequest cancel)
  {
    if (this.cancelled != null)
    {
      return;
    }
    this.cancelled = cancel;
    log.debug("IST got CANCEL");
    if (this.listener != null)
    {
      this.listener.onCancelled(cancel);
    }
    else
    {
      log.debug("No handler for processing INVITE");
    }
  }

}
