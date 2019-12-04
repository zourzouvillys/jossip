package com.jive.sip.dummer.txn;

import com.jive.sip.message.api.Reason;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.api.FlowId;

public interface SipClientTransaction extends SipTransaction
{

  /**
   * called to process this request. Called after constructing an instance.
   *
   * @param req
   * @return
   */

  void fromApplication(final SipRequest req, final FlowId flowId);

  /**
   * called when there is a response from the network.
   *
   * this will not be synchonised. implementations must be able to handle multiple threads calling it.
   *
   * @param res
   * @param flow
   */

  void fromNetwork(final SipResponse res, final FlowId flow);

  void cancel();

  /**
   * Cancels this transaction with a reason.
   *
   * @param reason
   */

  void cancel(final Reason reason);

}
