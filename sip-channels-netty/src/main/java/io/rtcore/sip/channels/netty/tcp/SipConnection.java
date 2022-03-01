package io.rtcore.sip.channels.netty.tcp;

import java.util.concurrent.CompletableFuture;

import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.netty.codec.SipFrame;
import io.rtcore.sip.channels.netty.codec.SipRequestFrame;

public interface SipConnection {

  /**
   * perform a SIP exchange over this connection.
   */

  SipStreamClientExchange exchange(SipRequestFrame req);

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
