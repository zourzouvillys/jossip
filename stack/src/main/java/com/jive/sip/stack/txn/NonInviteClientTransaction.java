package com.jive.sip.stack.txn;

import com.jive.sip.message.api.SipResponse;

/**
 * Domain mdoel for a single transaction.
 *
 * @author theo
 */

public class NonInviteClientTransaction extends AbstractTransactionService implements ClientTransaction, NonInviteTransaction
{

  @Override
  public void receiveResponse(final SipResponse res)
  {
    // TODO Auto-generated method stub

  }

}
