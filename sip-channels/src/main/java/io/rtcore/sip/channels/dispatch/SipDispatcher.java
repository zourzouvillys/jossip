package io.rtcore.sip.channels.dispatch;

import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import io.rtcore.sip.channels.SipServerCallHandler;
import io.rtcore.sip.message.message.SipRequest;

/**
 * multiple messages with the same transaction will only get dispatched once. a transaction will be
 * created if a response is marked as stateful.
 */

public final class SipDispatcher {

  private final SipServerCallHandler handler;

  private SipDispatcher(final SipServerCallHandler handler) {
    this.handler = handler;
  }

  /**
   * dispatch incoming requests to the specified call handler, ensuring multiple requests with the
   * same branch id don't get dispatched in parallel.
   */

  public static void dispatch(final Flow.Publisher<SipRequest> rx) {

    rx.subscribe(new Subscriber<SipRequest>() {

      private Subscription sub;

      @Override
      public void onSubscribe(final Subscription subscription) {
        this.sub = subscription;
        this.sub.request(1);
      }

      @Override
      public void onNext(final SipRequest item) {
        this.sub.request(1);
      }

      @Override
      public void onError(final Throwable throwable) {
        // todo: is this the best behavior?
        this.sub.cancel();
      }

      @Override
      public void onComplete() {
        // no more packets from this publisher.
      }

    });

  }

}
