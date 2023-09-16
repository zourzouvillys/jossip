package io.rtcore.sip.channels.api;

import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.frame.SipResponseFrame;

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
   * transmitted. if the INVITE has already been transmitted, a CANCEL must be sent.
   * 
   * @return true if this exchange was cancelled. the exchange will be marked as errored with
   *         Cancelled.
   * 
   */

  boolean cancel();

}
