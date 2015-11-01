package com.jive.sip.dummer.txn;


public interface ClientTransactionListener
{

  void onResponse(final SipTransactionResponseInfo res);

  void onError(final SipTransactionErrorInfo err);


}
