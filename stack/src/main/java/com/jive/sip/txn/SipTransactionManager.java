package com.jive.sip.txn;

import com.jive.sip.message.api.SipRequest;

/**
 * Primary interface for sending a transaction over the network.
 *
 * @author theo
 *
 */

public interface SipTransactionManager
{

  /**
   * Send a request over the network using a specified set of next hops.
   *
   * @param req
   *          The request to send.
   *
   * @param observer
   *          The observer which will receive associated events.
   *
   * @param options
   *          Transaction sending options.
   *
   * @param nexthop
   *
   * @return
   */

  SipTransactionHandle send(
      final SipRequest req,
      final SipClientTransactionObserver observer,
      final SipClientTransactionOptions options,
      final SipHostAndPort... nexthop);

}
