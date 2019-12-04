package com.jive.sip.dummer.txn;

import lombok.Value;

import com.jive.sip.message.api.SipResponse;
import com.jive.sip.transport.api.FlowId;

@Value
public class SipTransactionResponseInfo
{
  private FlowId flowId;
  private SipResponse response;
}
