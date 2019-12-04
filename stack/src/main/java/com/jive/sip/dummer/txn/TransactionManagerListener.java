package com.jive.sip.dummer.txn;

import com.jive.sip.message.api.SipMessage;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.api.FlowId;

public interface TransactionManagerListener
{

  void fromNetwork(final SipResponse res, final FlowId flow);

  void onResponseWithoutBranch(final SipResponse res, final FlowId flow);

  void onResponseWithoutCseq(final SipResponse res, final FlowId flow);

  void onUnknownResponse(final SipResponse res, final FlowId flow);

  void onNewTransaction(final SipRequest req, final FlowId flow, final SipClientTransaction txn);

  void onNewTransaction(final SipRequest req, final FlowId flow, final SipServerTransaction txn);

  void onSend(final FlowId flow, final SipMessage msg);

  void onRequestWithoutBranch(final SipRequest req, final FlowId flowId);

  void onCancel(final SipRequest req, final FlowId flowId);

  void onUnknownCancel(final SipRequest req, final FlowId flowId);

  void onRequest(final SipRequest req, final FlowId flowId);


}
