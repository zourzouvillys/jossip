package io.rtcore.sip.sigcore.txn;

import java.util.concurrent.TimeoutException;

import io.rtcore.sip.sigcore.txn.TransactionMessages.StartTransaction;

public interface NonInviteClientTxnBehavior {

  /**
   * 
   */

  void startTransaction(StartTransaction req);

  /**
   * send the original request over the transport for this transaction.
   * 
   * this will be called initially to send the request, and then periodically based on the timer
   * which is configured based on the settings for that specific transport instance.
   * 
   */

  void sendRequest();

  /**
   * notify the transaction user of a SIP response that has been generated from the original
   * request.
   * 
   * the ordering of these messages will strictly follow normal INVITE transaction SIP rules: 100?,
   * 1xx*, (2xx+ | [3-6]xx).
   * 
   * the transaction may not send duplicate 1xx which are not PRACK requests to the transaction
   * user, although this is not guaranteed.
   * 
   */

  void notifyTU(RxSipFrame msg);

  /**
   * notify the TU that there was an error soliciting a request.
   * 
   * reasons are numerous, including:
   * 
   * <pre>
   * - cancelled before request was transmitted to remote party
   * - timeout waiting for response
   * - transport unavailable (e.g, not configured, disallowed, not present, DNS failures)
   * - transport rate limited
   * - network errors (e.g transport closed, network unroutable, tls failures)
   * - other internal failures
   * </pre>
   * 
   */

  void notifyTU(Exception msg);

  // the "give up" timer.
  default void notifyTimeout() {
    notifyTU(new TimeoutException());
  }

}
