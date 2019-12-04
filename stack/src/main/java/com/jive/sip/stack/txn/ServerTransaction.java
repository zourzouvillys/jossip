package com.jive.sip.stack.txn;

import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.api.SipRequestReceivedEvent;

public interface ServerTransaction
{

  void fromNetwork(final SipRequestReceivedEvent e, final TransactionListener listener);

  void fromApp(final SipResponse res, final TransactionListener listener);

}
