package com.jive.sip.txn;

import com.jive.sip.dummer.txn.SipTransactionErrorInfo;
import com.jive.sip.dummer.txn.SipTransactionResponseInfo;

public interface SipClientTransactionObserver
{

  /**
   *
   */

  void onResponse(SipTransactionResponseInfo e);

  /**
   *
   */

  void onCompleted(SipTransactionErrorInfo e);

  /**
   *
   */

  void onError(Throwable th);

}
