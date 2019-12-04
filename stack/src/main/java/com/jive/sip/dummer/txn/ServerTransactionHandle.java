package com.jive.sip.dummer.txn;

import java.time.Instant;

import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.message.api.SipResponseStatus;
import com.jive.sip.processor.rfc3261.message.api.ResponseBuilder;
import com.jive.sip.transport.api.FlowId;

public interface ServerTransactionHandle
{

  void respond(final SipResponseStatus res);

  void respond(final ResponseBuilder res);

  void addListener(final ServerTransactionListener listener);

  void respond(final SipResponse build);

  SipRequest getRequest();

  FlowId getFlowId();

  Instant getCreationTime();

}
