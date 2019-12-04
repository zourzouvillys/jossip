package com.jive.sip.dummer.txn;

import java.util.LinkedList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.common.net.HostAndPort;
import com.jive.sip.message.api.BranchId;
import com.jive.sip.message.api.NameAddr;
import com.jive.sip.message.api.Reason;
import com.jive.sip.message.api.SipMessage;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.message.api.SipResponseStatus;
import com.jive.sip.transport.api.FlowId;

public interface TransactionRuntime extends TransactionListener
{

  void send(final FlowId flowId, final SipMessage msg);

  SipResponse createResponse(final SipRequest req, final SipResponseStatus status);

  HostAndPort getSelf(final FlowId flowId);

  SipMessage createAck(final SipResponse res, final LinkedList<NameAddr> newLinkedList);

  void createCancelTransaction(final FlowId flow, final SipRequest req, final BranchId branch, final Reason reason,
      final ClientTransactionListener listener);

  ScheduledFuture<?> schedule(final Runnable runnable, final long value, final TimeUnit unit);

  void logProcessingTime(final long ms);

}
