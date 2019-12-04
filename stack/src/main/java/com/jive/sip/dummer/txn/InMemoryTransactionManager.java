package com.jive.sip.dummer.txn;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.jive.sip.base.api.RawHeader;
import com.jive.sip.message.api.BranchId;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.Reason;
import com.jive.sip.message.api.SipMessage;
import com.jive.sip.message.api.SipMethod;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.message.api.SipResponseStatus;
import com.jive.sip.processor.rfc3261.SipMessageManager;
import com.jive.sip.transport.api.FlowId;
import com.jive.sip.transport.udp.UdpFlowId;
import com.jive.sip.transport.udp.UdpTransportListener;
import com.jive.sip.transport.udp.UdpTransportManager;

import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

/**
 * Keeps track of transactions, ensuring we retransmit as needed.
 *
 * FUTURE: hash based on branch id
 *
 *
 *
 * @author theo
 *
 */

@Slf4j
public class InMemoryTransactionManager implements TransactionManager, TransactionRuntime, TransactionListener,
    UdpTransportListener
{

  // we keep a single executor for scheduling.
  private final ScheduledExecutorService executor;

  private final UdpTransportManager transport;

  // TODO: partition this for less course locking.

  private final Map<BranchId, SipClientTransaction> icts = Maps.newHashMap();
  private final Map<BranchId, SipClientTransaction> nicts = Maps.newHashMap();

  private final Map<BranchId, SipServerTransaction> ists = Maps.newHashMap();
  private final Map<BranchId, SipServerTransaction> nists = Maps.newHashMap();

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  final SipStack stack;

  private final BranchGenerator branchGenerator;

  public InMemoryTransactionManager(
      final SipStack stack,
      final UdpTransportManager transport,
      final ScheduledExecutorService executor,
      final BranchGenerator gen)
  {
    this.stack = stack;
    this.executor = executor;
    this.transport = transport;
    this.branchGenerator = gen;
  }

  /**
   * An incoming SIP response should match an existing transaction.
   *
   * @param res
   *          The response received from the network
   *
   * @param flow
   *          The flow this message was received on
   *
   */

  @Override
  public void fromNetwork(final SipResponse res, final FlowId flow)
  {

    log.debug("SIP response: {}", res);

    // look at the top via header to find out txn
    final BranchId branch = checkNotNull(res.getBranchId());

    if (branch == null)
    {
      log.warn("Got response without branch");
      return;
    }
    else if (res.getCSeq() == null)
    {
      log.warn("response without CSeq: {}", res);
      return;
    }

    this.lock.readLock().lock();

    final SipClientTransaction txn;

    try
    {
      final Map<BranchId, SipClientTransaction> pool = this.getPool(res, branch);
      txn = pool.get(branch);
    }
    finally
    {
      this.lock.readLock().unlock();
    }

    // find the txn. if it doesn't exist, fail.

    if (txn == null)
    {
      log.warn("Got response for unknown txn {}: {} ({})", branch, res.getCSeq(), res);
      return;
    }

    // otherwise, feed into the txn so it can decide what to do.

    txn.fromNetwork(res, flow);

  }

  /**
   * returns the correct txn pool to use for the given request.
   *
   * @param res
   * @param branch
   * @return
   */

  private Map<BranchId, SipServerTransaction> getPool(final SipRequest req, final BranchId branch)
  {
    return (req.getMethod().equals(SipMethod.INVITE) || req.getMethod().equals(SipMethod.ACK)) ? this.ists : this.nists;
  }

  /**
   * returns the correct txn pool to use for the given response.
   *
   * @param res
   * @param branch
   * @return
   */

  private Map<BranchId, SipClientTransaction> getPool(final SipResponse res, final BranchId branch)
  {
    return (res.getCSeq().getMethod().equals(SipMethod.INVITE)) ? this.icts : this.nicts;
  }

  /**
   * returns the correct txn pool for the given transaction.
   */

  private Map<BranchId, ? extends SipTransaction> getPool(final SipTransaction txn)
  {
    if (txn instanceof DatagramInviteClientTransaction)
    {
      return this.icts;
    }
    else if (txn instanceof DatagramNonInviteClientTransaction)
    {
      return this.nicts;
    }
    else if (txn instanceof DatagramInviteServerTransaction)
    {
      return this.ists;
    }
    else if (txn instanceof DatagramNonInviteServerTransaction)
    {
      return this.nists;
    }
    throw new RuntimeException("Unknown Transaction Type");
  }

  /**
   * creates a transaction, and sends the request.
   *
   * we are responsible for adding the Via. The application should not create one - although any that is added will be passed back in the
   * response, so can be used for stateless data if needed.
   *
   * @param listener
   * @param ops
   * @return
   *
   */

  @Override
  public SipClientTransaction fromApplication(final SipRequest req, final FlowId flow,
      final ClientTransactionListener listener,
      final ClientTransactionOptions ops)
  {

    Preconditions.checkArgument(req.getCSeq() != null);
    Preconditions.checkArgument(req.getMethod().equals(req.getCSeq().getMethod()),
        "CSeq method must match SIP request method.");

    // generate a branch
    final BranchId branch = BranchId.withCookiePrepended(this.generateBranch());

    final boolean isInvite = (req.getMethod().equals(SipMethod.INVITE));

    log.debug("Adding outgoing branch for {} {} dest={}, ops={}", req, branch.getValue(), flow, ops);

    final SipClientTransaction txn = (isInvite)
        ? new DatagramInviteClientTransaction(this, branch, listener, ops)
        : new DatagramNonInviteClientTransaction(this, flow, branch, listener, ops);

    this.lock.writeLock().lock();

    try
    {
      (isInvite ? this.icts : this.nicts).put(branch, txn);
    }
    finally
    {
      this.lock.writeLock().unlock();
    }

    txn.fromApplication(req, flow);

    return txn;

  }

  private CharSequence generateBranch()
  {
    final StringBuilder sb = new StringBuilder();
    sb.append(this.branchGenerator.getPrefix());
    sb.append(TransactionUtils.randomAlphanumeric(16));
    return sb.toString();
  }

  @Override
  public void send(final FlowId flow, final SipMessage msg)
  {
    log.trace("Sending {} to {}", msg.toString(), flow);
    this.transport.send((UdpFlowId) flow, msg);
  }

  /**
   * returns the name to use in a Via header.
   */

  @Override
  public HostAndPort getSelf(final FlowId flowId)
  {
    if (InetAddresses.isInetAddress(this.stack.getSelf().getHost()))
    {
      return this.stack.getSelf().withDefaultPort(this.transport.getPort((UdpFlowId) flowId));
    }
    return this.stack.getSelf();
  }

  @Override
  public void fromNetwork(final SipRequest req, final FlowId flowId)
  {

    // look at the top via header to find out txn
    final BranchId branch = checkNotNull(req.getBranchId());

    if (branch == null)
    {
      log.warn("Got response without branch");
      return;
    }

    final Map<BranchId, SipServerTransaction> pool = this.getPool(req, branch);

    SipServerTransaction txn;

    this.lock.readLock().lock();
    try
    {
      txn = pool.get(branch);
    }
    finally
    {
      this.lock.readLock().unlock();
    }

    if (txn == null)
    {

      if (req.getMethod().equals(SipMethod.CANCEL))
      {

        // find the other txn.
        txn = this.ists.get(branch);

        if (txn == null)
        {
          log.warn("Got CANCEL for unknown txn {}", branch);
          this.transport
              .send((UdpFlowId) flowId, this.stack.createResponse(req, SipResponseStatus.CALL_DOES_NOT_EXIST));
          return;
        }

        this.transport.send((UdpFlowId) flowId, this.stack.createResponse(req, SipResponseStatus.OK));
        ((DatagramInviteServerTransaction) txn).cancel(req);
        return;

      }
      else if (req.getMethod().equals(SipMethod.ACK))
      {

        // no txn, so pass to the handler.
        this.stack.getInviteServerHandler().processAck(req, flowId);
        return;

      }

      txn = this.createServerTxn(req, flowId, branch);

      if (txn == null)
      {
        log.warn("Rejecting request without handler: {}", req);
        // statelessly reject.
        this.transport.send((UdpFlowId) flowId, this.stack.createResponse(req, SipResponseStatus.METHOD_NOT_ALLOWED));
        return;
      }

      this.lock.writeLock().lock();
      try
      {
        pool.put(branch, txn);
      }
      finally
      {
        this.lock.writeLock().unlock();
      }

    }

    txn.fromNetwork(req, flowId);

  }

  /**
   * Constructs a new transaction for an incoming request we don't know anything about.
   *
   * @param req
   * @param flowId
   * @param branch
   * @return
   */

  private SipServerTransaction createServerTxn(final SipRequest req, final FlowId flowId, final BranchId branch)
  {

    if (req.getMethod().equals(SipMethod.INVITE))
    {

      final InviteServerTransactionHandler handler = this.stack.getInviteServerHandler();

      if (handler == null)
      {
        return null;
      }

      return new DatagramInviteServerTransaction(this, flowId, handler);

    }
    else
    {

      final NonInviteServerTransactionHandler handler = this.stack.getHandler(req.getMethod());

      if (handler == null)
      {
        return null;
      }

      return new DatagramNonInviteServerTransaction(this, flowId, handler);

    }

  }

  /**
   * Creates a txn for CANCEL processing, and sends it.
   *
   * @param flow
   * @param req
   */

  @Override
  public void createCancelTransaction(final FlowId flow, final SipRequest req, final BranchId branch,
      final Reason reason, final ClientTransactionListener listener)
  {
    SipRequest cancel = this.getMessageManager().createCancel(req, reason);
    if (this.stack.getServerName() != null)
    {
      cancel = (SipRequest) cancel.withHeader(new RawHeader("User-Agent", this.stack.getServerName()));
    }
    // TODO: we should copy the timer options from the INVITE? -- tpz
    final DatagramNonInviteClientTransaction txn = new DatagramNonInviteClientTransaction(this, flow, branch, listener, null);

    this.lock.writeLock().lock();
    try
    {
      this.nicts.put(branch, txn);
    }
    finally
    {
      this.lock.writeLock().unlock();
    }

    log.debug("Sending CANCEL for {}", branch);
    txn.fromApplication(cancel, flow);
  }

  private SipMessageManager getMessageManager()
  {
    return this.stack.getMessageManager();
  }

  @Override
  public SipResponse createResponse(final SipRequest req, final SipResponseStatus status)
  {
    return this.stack.createResponse(req, status);
  }

  @Override
  public SipMessage createAck(final SipResponse res, final LinkedList<NameAddr> route)
  {
    SipMessage msg = this.getMessageManager().createAck(res, route);
    if (this.stack.getServerName() != null)
    {
      msg = msg.withHeader(new RawHeader("User-Agent", this.stack.getServerName()));
    }
    return msg;
  }

  /**
   * Remove the transaction when it's terminated.
   */

  @Override
  public void onTerminated(final SipTransaction txn)
  {
    log.trace("Removing Transaction");
    final SipTransaction item = this.getPool(txn).remove(txn.getBranchId());
    if (item == null)
    {
      log.warn("Couldn't find txn {} to remove", txn);
    }
  }

  @Override
  public ScheduledFuture<?> schedule(final Runnable runnable, final long delay, final TimeUnit unit)
  {
    return this.executor.schedule(runnable, delay, unit);
  }

  public long getActiveTransactions(final TransactionType type)
  {
    switch (type)
    {
      case InviteClient:
        return this.icts.size();
      case InviteServer:
        return this.ists.size();
      case NonInviteClient:
        return this.nicts.size();
      case NonInviteServer:
        return this.nists.size();
      default:
        throw new RuntimeException("Unknown Transaction Type");

    }
  }

  public Executor getExecutor()
  {
    return this.executor;
  }

  @Override
  public void onSipRequestReceived(final UdpFlowId flow, final InetSocketAddress sender, final SipRequest msg)
  {
    try
    {
      this.fromNetwork(msg, flow);
    }
    catch (final Exception e)
    {
      log.warn("Exception processing request", e);
      log.warn("Exception message payload: [{}]", new String(this.stack.messages.toBytes(msg).array(), Charsets.UTF_8));
    }
  }

  @Override
  public void onSipResponseReceived(final UdpFlowId flow, final InetSocketAddress sender, final SipResponse msg)
  {
    try
    {
      this.fromNetwork(msg, flow);
    }
    catch (final Exception e)
    {
      log.warn("Exception processing response", e);
      log.warn("Exception message payload: [{}]", new String(this.stack.messages.toBytes(msg).array(), Charsets.UTF_8));
    }
  }

  @Override
  public void onInvalidSipMessageEvent(final UdpFlowId createFlowId, final InetSocketAddress sender)
  {
    log.warn("Received unparsabe SIP message from {}", sender);
  }

  @Override
  public void onKeepalive(final UdpFlowId flow, final InetSocketAddress sender)
  {
    this.transport.sendKeepalive(flow);
  }

  @Override
  public void logProcessingTime(final long ms)
  {
  }

  @Override
  public void onStunPacket(final UdpFlowId flow, final DatagramPacket pkt)
  {
    // err, nothing.
  }

}
