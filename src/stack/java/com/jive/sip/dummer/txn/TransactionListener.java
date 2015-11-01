package com.jive.sip.dummer.txn;

/**
 * Allows components to listen in on a transaction's lifecycle
 * 
 * @author theo
 * 
 */

public interface TransactionListener
{

  /**
   * Called when the transaction is terminated and no more messages are expected to be received or sent on it.
   * 
   * @param txn
   *          The {@link SipTransaction}.
   */

  void onTerminated(final SipTransaction txn);

}
