package com.jive.sip.dummer.txn;

import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.api.FlowId;

public interface SipMessageHandler
{

  void processRequest(final SipRequest req, final FlowId flowId);

  void processResponse(final SipResponse res, final FlowId flowId);

}
