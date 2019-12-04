package com.jive.sip.stack.txn;

import java.time.Duration;

import com.jive.sip.message.api.BranchId;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.api.FlowId;

public interface TransactionListener
{

  /**
   * Sends the response to the network.
   *
   * @param flowId
   * @param response
   *
   */

  void sendToNetwork(final FlowId flowId, final SipResponse response);

  /**
   * Dispatch the messages to the app stack.
   *
   * @param message
   */

  void sendToApp(final CorrelationId correlationId, final SipRequest message);

  /**
   *
   */

  void schedule(final BranchId branchId, final Duration duration);

}
