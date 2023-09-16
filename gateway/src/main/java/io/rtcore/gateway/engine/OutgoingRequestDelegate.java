package io.rtcore.gateway.engine;

import io.rtcore.sip.frame.SipResponseFrame;

public interface OutgoingRequestDelegate {

  /**
   * called for each SIP response received.
   *
   * @param res
   *          The SIP response message.
   *
   */

  void onResponse(SipResponseFrame res);

  /**
   * Method invoked when it is known that no additional method invocations will occur for a outgoing
   * request that is not already terminated by error, after which no other methods are invoked on
   * the delegate. If this method throws an exception, resulting behavior is undefined.
   */

  void onComplete();

  /**
   * Method invoked upon an unrecoverable error encountered by a Publisher or Subscription, after
   * which no other Subscriber methods are invoked by the Subscription. If this method itself throws
   * an exception, resulting behavior is undefined.
   *
   * @param throwable
   *          the exception
   */

  void onError(Throwable t);

}
