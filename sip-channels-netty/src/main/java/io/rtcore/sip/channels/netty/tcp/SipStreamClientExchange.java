package io.rtcore.sip.channels.netty.tcp;

import java.util.concurrent.CompletableFuture;

import com.google.common.base.Verify;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.UnicastProcessor;
import io.rtcore.sip.channels.connection.SipClientExchange;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipRequestFrame;
import io.rtcore.sip.channels.connection.SipResponseFrame;
import io.rtcore.sip.channels.netty.tcp.TlsSipConnection.ClientBranchId;
import io.rtcore.sip.common.iana.SipMethods;

class SipStreamClientExchange implements SipClientExchange {

  private final SipConnection conn;
  private final SipRequestFrame req;

  // all of the client responses.
  private final UnicastProcessor<Event> receiver;

  // the transport transmission.
  private final CompletableFuture<?> send;

  // the branch we are sending with
  private final ClientBranchId branchId;

  /**
   * @param conn
   * @param req
   * @param branchId
   */

  SipStreamClientExchange(SipConnection conn, SipRequestFrame req, ClientBranchId branchId) {

    // can't have an ACK for a transaction.
    Verify.verify(req.initialLine().method() != SipMethods.ACK);

    this.conn = conn;
    this.req = req;
    this.receiver = UnicastProcessor.create(true);
    this.branchId = branchId;

    // request the transport transmit this request.
    this.send = this.conn.send(this.req);

    // then completing exceptionally, we 
    this.send.exceptionally(ex -> {
      this.receiver.onError(ex);
      return null;
    });

  }

  /**
   * 
   */

  void onResponseFrame(TlsSipConnection conn, SipResponseFrame frame) {

    if (req.initialLine().method() == SipMethods.INVITE) {

      this.receiver.onNext(new Event(conn, frame));

      if (frame.initialLine().code() >= 300) {
        this.receiver.onComplete();
      }

    }
    else {

      this.receiver.onNext(new Event(conn, frame));

      if (frame.initialLine().code() >= 200) {
        this.receiver.onComplete();
      }

    }

  }

  /**
   * the request which is being sent over this connection.
   */

  @Override
  public SipRequestFrame request() {
    return this.req;
  }

  /**
   * the responses that are received for this exchange. it will complete once the transaction
   * completes (or is cancelled), and error if the transport fails.
   */

  @Override
  public Flowable<Event> responses() {
    return this.receiver;
  }

  /**
   * the SipConnection which this exchange is happening over.
   */

  @Override
  public SipConnection connection() {
    return this.conn;
  }

  /**
   * attempt to cancel sending the request. a request can only be cancelled if it has not yet been
   * transmitted.
   */

  @Override
  public boolean cancel() {
    return this.send.cancel(false);
  }

}
