package com.jive.sip.dummer.txn;

import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.api.FlowId;

public interface TransactionManager
{

  public SipClientTransaction fromApplication(final SipRequest req, final FlowId flow, final ClientTransactionListener listener,
      final ClientTransactionOptions ops);


  public void fromNetwork(final SipRequest req, final FlowId flow);

  public void fromNetwork(final SipResponse res, final FlowId flow);

}
