package com.jive.sip.dummer.txn;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;
import com.jive.sip.message.api.BranchId;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.message.api.SipResponseStatus;
import com.jive.sip.processor.rfc3261.message.api.ResponseBuilder;
import com.jive.sip.transport.api.FlowId;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatagramNonInviteServerTransaction extends AbstractDatagramTransaction implements SipServerTransaction, ServerTransactionHandle
{

  private static enum State
  {
    Trying,
    Proceeding,
    Completed,
    Terminated
  }

  private State state = null;

  private final Stopwatch timer = Stopwatch.createUnstarted();

  private static enum Timer
  {
    TimerJ
  }

  private final NonInviteServerTransactionHandler handler;
  private SipRequest req;
  private FlowId flowId;
  private SipResponse res;
  private final TransactionRuntime runtime;
  private final Instant created = Instant.now();

  private ScheduledFuture<?> timerJ;

  public DatagramNonInviteServerTransaction(final TransactionRuntime runtime, final FlowId flowId, final NonInviteServerTransactionHandler handler)
  {
    super(runtime);
    this.runtime = runtime;
    this.handler = handler;
    this.flowId = flowId;
  }

  @Override
  public void fromApplication(final SipResponse req)
  {
  }

  @Override
  public void fromNetwork(final SipRequest req, final FlowId flow)
  {

    if (this.state == null)
    {
      setState(State.Trying);
      this.req = req;
      this.flowId = flow;
      this.timer.start();
      this.handler.processRequest(this);
      return;
    }

    switch (this.state)
    {
      case Trying:
        log.debug("Slow NIST? Got {} from {} in Trying", req, flow);
        return;
      case Proceeding:
      case Completed:
        log.debug("Retransmitting NIST response: {}", this.res);
        this.runtime.send(flow, this.res);
        break;
      case Terminated:
        break;
    }

  }


  private void setState(final State state)
  {
    if (this.state == state)
    {
      return;
    }
    log.debug("State: {} -> {}", this.state, state);
    this.state = state;
  }

  @Override
  public void respond(final SipResponseStatus status)
  {
    respond(this.runtime.createResponse(this.req, status));
  }

  @Override
  public void respond(final ResponseBuilder res)
  {
    respond(res.build(this.req));
  }

  @Override
  public void respond(final SipResponse res)
  {

    final SipResponseStatus status = res.getStatus();

    switch (this.state)
    {
      case Trying:
      case Proceeding:
        if (status.isFinal())
        {
          final long ms = this.timer.stop().elapsed(TimeUnit.MILLISECONDS);
          log.debug("NIST processed in {} ms", ms);
          this.runtime.logProcessingTime(ms);
          setState(State.Completed);
          if (res.getStatus().isFinal())
          {
            schedule(Timer.TimerJ, T1.multipliedBy(64));
          }
        }
        else
        {
          setState(State.Proceeding);
        }
        this.res = res;
        this.runtime.send(this.flowId, this.res);
        break;
      case Completed:
      case Terminated:
        throw new IllegalStateException(String.format("Can't send %s in %s", status, this.state));
    }


    // TODO: set expires timer.
  }

  @Override
  public void addListener(final ServerTransactionListener listener)
  {
    // we don't actually do anyhting right now, kind of pointless to listen to this until we have better transport
    // support.
  }


  @Override
  public SipRequest getRequest()
  {
    return this.req;
  }

  @Override
  public FlowId getFlowId()
  {
    return this.flowId;
  }

  private void fire(final Timer timer)
  {

    log.debug("Timer fired: {}", timer);

    switch (timer)
    {
      case TimerJ:
        this.runtime.onTerminated(this);
        break;
    }

  }

  /**
   *
   * @param timer
   * @param duration
   * @return
   */

  private ScheduledFuture<?> schedule(final Timer timer, final Duration duration)
  {
    return this.runtime.schedule(new Runnable()
    {
      @Override
      public void run()
      {
        fire(timer);
      }
    }, duration.getNano(), TimeUnit.NANOSECONDS);
  }

  @Override
  public BranchId getBranchId()
  {
    return this.req.getBranchId();
  }

  @Override
  public Instant getCreationTime()
  {
    return this.created;
  }


}
