package com.jive.sip.dummer.txn;

import java.time.Instant;

import com.jive.sip.message.api.BranchId;
import com.jive.sip.message.api.SipRequest;
import com.jive.sip.message.api.SipResponse;
import com.jive.sip.message.api.SipResponseStatus;
import com.jive.sip.processor.rfc3261.message.api.ResponseBuilder;
import com.jive.sip.transport.api.FlowId;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDatagramServerTransaction extends AbstractDatagramTransaction implements SipServerTransaction, ServerTransactionHandle
{

  protected SipRequest req;
  protected SipResponse res;
  protected FlowId flowId;
  protected final Instant created = Instant.now();

  public AbstractDatagramServerTransaction(final TransactionRuntime manager, final FlowId flowId)
  {
    super(manager);
    this.flowId = flowId;
  }


  @Override
  public SipRequest getRequest()
  {
    return this.req;
  }

  @Override
  public FlowId getFlowId()
  {
    return this.flowId;
  }

  @Override
  public BranchId getBranchId()
  {
    return this.req.getBranchId();
  }

  @Override
  public Instant getCreationTime()
  {
    return this.created;
  }


  @Override
  public void respond(final SipResponseStatus status)
  {
    log.debug("Responding with {}", status);
    respond(this.manager.createResponse(this.req, status));
  }

  @Override
  public void respond(final ResponseBuilder res)
  {
    respond(res.build(this.req));
  }


}
