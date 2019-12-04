package com.jive.sip.dummer.txn;

import java.time.Instant;

import com.jive.sip.message.api.BranchId;


public interface SipTransaction
{

  Instant getCreationTime();

  BranchId getBranchId();

}
