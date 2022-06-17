package io.rtcore.sip.channels.connection;

import io.reactivex.rxjava3.core.Flowable;

public interface SipClientExchange extends SipExchange {

  public static record Event(SipConnection connection, SipResponseFrame response) {
  }

  /**
   * the responses that are received for this exchange. it will complete once the transaction
   * completes (or is cancelled).
   */

  Flowable<Event> responses();

  /**
   * attempt to cancel sending the request. a request can only be cancelled if it has not yet been
   * transmitted.
   */

  boolean cancel();

}