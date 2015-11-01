package com.jive.sip.dummer.txn;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Preconditions;
import com.jive.sip.dummer.txn.SipTransactionErrorInfo.ErrorCode;
import com.jive.sip.message.api.BranchId;
import com.jive.sip.message.api.Reason;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.message.api.Via;
import com.jive.sip.message.api.ViaProtocol;
import com.jive.sip.transport.api.FlowId;

import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of a client non-INVITE transaction over an unreliable transport.
 *
 * @author theo
 *
 */
@Slf4j
public class DatagramNonInviteClientTransaction extends AbstractDatagramTransaction implements SipClientTransaction
{

  private static enum Timer
  {
    TimerE,
    TimerF,
    TimerK,
  }

  private static enum State
  {
    Trying,
    Proceeding,
    Completed,
    Terminated
  }

  private final FlowId flow;
  private final TransactionRuntime manager;
  private SipRequest req;
  private int retries = 0;
  private final BranchId branch;
  private final ClientTransactionListener listener;
  private State state = null;
  private final Instant created = Instant.now();

  private ScheduledFuture<?> timerE;
  private ScheduledFuture<?> timerF;
  private ScheduledFuture<?> timerK;

  public DatagramNonInviteClientTransaction(final TransactionRuntime manager, final FlowId flow, final BranchId branch,
      final ClientTransactionListener listener, final ClientTransactionOptions ops)
  {
    super(manager);
    this.manager = manager;
    this.flow = flow;
    this.branch = branch;
    this.listener = listener;
  }

  /**
   * After the initial request, we should never see any other messages from the application.
   */

  @Override
  public void fromApplication(final SipRequest req, final FlowId flow)
  {

    Preconditions.checkState(this.state == null);

    this.setState(State.Trying);

    log.debug("Using branch {}", this.branch.getValue());

    this.req = TransactionUtils.addVia(req, this.branch, this.manager, ViaProtocol.UDP, flow);

    this.manager.send(flow, this.req);

    this.timerE = this.schedule(Timer.TimerE, T1);
    this.timerF = this.schedule(Timer.TimerF, T1.multipliedBy(64));

  }

  @Override
  public void fromNetwork(SipResponse res, final FlowId flow)
  {

    log.debug("Got response on NICT: {}", res);

    // remove the top via header.

    // TODO: we should add a prettier way of doing this. probably also in the manager, not here.
    final List<Via> vias = res.getVias();

    if (!vias.isEmpty())
    {
      vias.remove(0);
      res = res.withoutHeaders("Via", "v");
      res = res.withParsed("Via", vias);
    }

    switch (this.state)
    {

      case Trying:

        if (!res.getStatus().isFinal())
        {
          this.setState(State.Proceeding);
          if (this.listener != null)
          {
            this.listener.onResponse(new SipTransactionResponseInfo(flow, res));
          }
        }
        else
        {
          if (this.listener != null)
          {
            this.listener.onResponse(new SipTransactionResponseInfo(flow, res));
          }
          this.setState(State.Completed);
        }
        break;

      case Proceeding:

        if (!res.getStatus().isFinal())
        {
          this.setState(State.Proceeding);
          if (this.listener != null)
          {
            this.listener.onResponse(new SipTransactionResponseInfo(flow, res));
          }
        }
        else
        {
          if (this.listener != null)
          {
            this.listener.onResponse(new SipTransactionResponseInfo(flow, res));
          }
          this.setState(State.Completed);
        }

        break;

      case Completed:
      case Terminated:
        // ignored response.
        log.debug("Ignoring NICT response in state {}", this.state);
        break;

      default:
        throw new RuntimeException(String.format("Unsupported NICT State %s", this.state));

    }

  }

  private void fire(final Timer timer)
  {

    log.debug("Timer fired: {}", timer);

    switch (timer)
    {

      case TimerE:

        switch (this.state)
        {
          case Trying:
          case Proceeding:
            this.manager.send(this.flow, this.req);
            this.timerE =
                this.schedule(Timer.TimerE,
                    Duration.ofMillis(Math.min((++this.retries) * T1.toMillis(), T2.toMillis())));
            break;
          case Completed:
          case Terminated:
            break;
        }
        break;

      case TimerF:
        switch (this.state)
        {
          case Trying:
          case Proceeding:
            if (this.listener != null)
            {
              this.listener.onError(new SipTransactionErrorInfo(ErrorCode.Timeout));
            }
            this.setState(State.Terminated);
            break;
          case Completed:
            // ignore it.
            break;
          case Terminated:
            // ignore it.
            break;
        }
        break;

      case TimerK:
        switch (this.state)
        {
          case Trying:
          case Proceeding:
            break;
          case Completed:
            this.setState(State.Terminated);
            break;
          case Terminated:
            // ignore it.
            break;
        }
        break;
      default:
        break;
    }
  }

  /**
   *
   */

  private void setState(final State state)
  {
    if (this.state != state)
    {
      log.trace("NICT: {} -> {}", this.state, state);
      final State oldState = this.state;
      this.state = state;
      this.enterState(oldState, this.state);
    }
  }

  private void enterState(final State oldState, final State newState)
  {

    switch (newState)
    {

      case Trying:
        break;

      case Proceeding:
        break;

      case Completed:

        if (this.timerF != null)
        {
          this.timerF.cancel(true);
          this.timerF = null;
        }

        if (this.timerE != null)
        {
          this.timerE.cancel(true);
          this.timerE = null;
        }

        this.timerK = this.schedule(Timer.TimerK, T4);
        break;

      case Terminated:

        if (this.timerE != null)
        {
          this.timerE.cancel(true);
          this.timerE = null;
        }

        this.manager.onTerminated(this);
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
    return this.manager.schedule(() -> DatagramNonInviteClientTransaction.this.fire(timer), duration.toMillis(), TimeUnit.MILLISECONDS);
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

  @Override
  public void cancel(final Reason reason)
  {
    // nothing we can do here. stopping transmitting is potentially more damaging than continuing...
    log.debug("Attempted to cancel NICT");
  }

  @Override
  public void cancel()
  {
    this.cancel(null);
  }

  @Override
  public String toString()
  {
    return new StringBuilder("NICT(")
    .append("state=").append(this.state).append(", ")
    .append("req=").append(this.req)
    .append(")")
    .toString();
  }

}
