package com.jive.sip.stack.txn;

import com.jive.sip.transport.api.SipRequestReceivedEvent;
import com.jive.sip.transport.api.SipResponseReceivedEvent;

public interface ClientTransactionService extends TransactionService
{

  /**
   * Called when a SIP response has been received from the network.
   * 
   * @param e
   */

  void fromNetwork(final SipResponseReceivedEvent e);

  /**
   * Called when a SIP request has been received from the transaction user.
   */

  void fromTransactionUser(final SipRequestReceivedEvent e);

}
