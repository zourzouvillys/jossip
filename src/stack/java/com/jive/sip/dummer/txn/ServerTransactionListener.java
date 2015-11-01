package com.jive.sip.dummer.txn;

import com.jive.sip.message.api.SipRequest;

public interface ServerTransactionListener
{

  void onCancelled(final SipRequest cancel);

}
