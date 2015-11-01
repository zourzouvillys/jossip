package com.jive.sip.dummer.txn;

import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.api.FlowId;

public interface SipServerTransaction extends SipTransaction
{

  void fromApplication(final SipResponse req);

  void fromNetwork(final SipRequest req, final FlowId flow);

}
