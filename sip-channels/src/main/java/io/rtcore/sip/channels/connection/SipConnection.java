package io.rtcore.sip.channels.connection;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import io.reactivex.rxjava3.core.Flowable;

public interface SipConnection {

  /**
   * the close future.
   */

  CompletionStage<?> closeFuture();

  /**
   * perform a SIP exchange over this connection.
   */

  SipClientExchange exchange(SipRequestFrame req);

  /**
   * send a single SIP frame, as is.
   */

  CompletableFuture<?> send(SipFrame frame);

  /**
   * tap the connection
   */

  Flowable<SipFrame> frames();

  /**
   * close the connection.
   */

  void close();

}
