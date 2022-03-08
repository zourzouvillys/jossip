package io.rtcore.sip.channels.api;

import java.util.concurrent.CompletionStage;

public interface SipServerExchange extends SipExchange {

  /**
   * implemented by the server handler for an incoming exchange over a socket.
   */

  public interface Listener {

    /**
     * The call was cancelled and the server is encouraged to abort processing to save resources,
     * since the client will not process any further messages. Cancellations can be caused by
     * timeouts, explicit cancellation by the client, network errors, etc.
     *
     * <p>
     * There will be no further callbacks for the call.
     */

    void onCancel();

  }

  /**
   * 
   */

  void onError(Throwable error);

  /**
   * mark this call as completed.
   */

  void onComplete();

  /**
   * 
   */

  boolean isCancelled();

  /**
   * sends a response frame that will be responded to using standard SIP rules, and the completion
   * stage marked as done once network stack signals it in the socket rx queue.
   */

  CompletionStage<?> onNext(SipResponseFrame response);

}
