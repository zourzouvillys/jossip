package com.jive.sip.dummer.txn;

import com.jive.sip.message.api.SipRequest;
import com.jive.sip.transport.api.FlowId;


public interface InviteServerTransactionHandler extends NonInviteServerTransactionHandler
{

  void processAck(final SipRequest req, final FlowId flowId);

}
