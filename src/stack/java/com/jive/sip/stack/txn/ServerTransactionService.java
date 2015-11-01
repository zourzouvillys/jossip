package com.jive.sip.stack.txn;

import com.jive.sip.transport.api.SipRequestReceivedEvent;
import com.jive.sip.transport.api.SipResponseReceivedEvent;

public interface ServerTransactionService extends TransactionService
{

  /**
   * Called when a SIP request has been received from the transaction user.
   * 
   * @param e
   */

  void fromTransactionUser(final SipResponseReceivedEvent e);

  /**
   * Called when a SIP response has been received from the network.
   */

  void fromNetwork(final SipRequestReceivedEvent e);

}
