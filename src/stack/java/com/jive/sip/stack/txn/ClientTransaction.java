package com.jive.sip.stack.txn;

import com.jive.sip.message.api.SipResponse;

public interface ClientTransaction
{

  void receiveResponse(final SipResponse res);

}
