package io.rtcore.sip.channels.connection;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipChannel;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.channels.api.SipFrame;
import io.rtcore.sip.channels.api.SipRequestFrame;

public interface SipConnection extends SipChannel {

  /**
   * Connection attributeds.
   */

  SipAttributes attributes();

  /**
   * the close future.
   */

  CompletionStage<?> closeFuture();

  /**
   * perform a SIP exchange over this connection.
   */

  @Override
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
