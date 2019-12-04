package com.jive.sip.stack.txn.mem;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Duration;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.Maps;
import com.jive.sip.message.api.BranchId;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.stack.txn.AbstractTransactionService;
import com.jive.sip.stack.txn.CorrelationId;
import com.jive.sip.stack.txn.NonInviteServerTransaction;
import com.jive.sip.stack.txn.ServerTransactionService;
import com.jive.sip.stack.txn.TransactionListener;
import com.jive.sip.transport.api.FlowId;
import com.jive.sip.transport.api.SipRequestReceivedEvent;
import com.jive.sip.transport.api.SipResponseReceivedEvent;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author theo
 */

@Slf4j
public class InMemoryServerTransactionService extends AbstractTransactionService implements ServerTransactionService, TransactionListener
{

  /**
   * The map used for storing the transactions.
   */

  private final ConcurrentMap<BranchId, NonInviteServerTransaction> nists = Maps.newConcurrentMap();

  /**
   * 
   */

  @Override
  public void fromNetwork(final SipRequestReceivedEvent e)
  {

    checkNotNull(e);

    final BranchId branch = checkNotNull(e.getMessage().getBranchId());

    if (branch == null)
    {
      // nothing we can do about this.
      // TODO:TPZ: notify listeners
      return;
    }

    NonInviteServerTransaction nist = this.nists.get(branch);

    if (nist != null)
    {

      nist.fromNetwork(e, this);

      log.debug("Retransmission received");

      // this transaction exists, so we either need to retransmit the response as it's already been
      // responded to, or it's being processed in which case we just ignore it.

    }
    else
    {

      // create a new server transaction

      nist = new NonInviteServerTransaction(branch);

      this.nists.put(branch, nist);

      // and dispatch into the Executor, potentially limiting the number of new transactions per second we're creating.

      log.debug("Got new transaction");

    }

  }

  /**
   * 
   */

  @Override
  public void fromTransactionUser(final SipResponseReceivedEvent e)
  {

    final BranchId branch = e.getMessage().getBranchId();

    if (branch == null)
    {
      // nothing we can do about this.
      // TODO:TPZ: notify listeners
      return;
    }

    log.debug("Received response from TU");

  }

  @Override
  public void sendToNetwork(final FlowId flowId, final SipResponse response)
  {
    // TODO Auto-generated method stub

  }

  @Override
  public void sendToApp(final CorrelationId correlationId, final SipRequest message)
  {
    // TODO Auto-generated method stub
  }

  @Override
  public void schedule(final BranchId branchId, final Duration duration)
  {
    // TODO Auto-generated method stub
  }

}
