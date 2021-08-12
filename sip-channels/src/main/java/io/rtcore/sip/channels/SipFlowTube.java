package io.rtcore.sip.channels;

import java.util.concurrent.Flow;

/**
 * SipFlowTube is an I/O abstraction that allows reading from and writing to a destination
 * asynchronously. This is not a {@link Flow.Processor Flow.Processor&lt;SipMessage,
 * SipMessage&gt;}, but rather models a publisher source and a subscriber sink in a bidirectional
 * flow.
 * <p>
 * The {@code connectFlows} method should be called to connect the bidirectional flow. A SipFlowTube
 * supports handing over the same read subscription to different sequential read SipFlowTube over
 * time. When {@code connectFlows(writePublisher, readSubscriber} is called, the SipFlowTube will
 * call {@code dropSubscription} on its former readSubscriber, and {@code onSubscribe} on its new
 * readSubscriber.
 */

public interface SipFlowTube<OutT, InT> extends Flow.Publisher<InT>, Flow.Subscriber<OutT> {

  /**
   * A subscriber for reading from the bidirectional flow. A TubeSubscriber is a
   * {@code Flow.Subscriber} that can be canceled by calling {@code dropSubscription()}. Once
   * {@code dropSubscription()} is called, the {@code TubeSubscriber} should stop calling any method
   * on its subscription.
   */

  interface TubeSubscriber<T> extends Flow.Subscriber<T> {

    /**
     * Called when the flow is connected again, and the subscription is handed over to a new
     * subscriber. Once {@code dropSubscription()} is called, the {@code TubeSubscriber} should stop
     * calling any method on its subscription.
     */

    default void dropSubscription() {
    }

  }

  /**
   * A publisher for writing to the bidirectional flow.
   */

  interface TubePublisher<T> extends Flow.Publisher<T> {
  }

  /**
   * Connects the bidirectional flows to a write {@code Publisher} and a read {@code Subscriber}.
   * This method can be called sequentially several times to switch existing publishers and
   * subscribers to a new pair of write subscriber and read publisher.
   *
   * @param writePublisher
   *          A new publisher for writing to the bidirectional flow.
   * @param readSubscriber
   *          A new subscriber for reading from the bidirectional flow.
   */

  default void connectFlows(final TubePublisher<OutT> writePublisher, final TubeSubscriber<InT> readSubscriber) {
    this.subscribe(readSubscriber);
    writePublisher.subscribe(this);
  }

  /**
   * Returns true if this flow was completed, either exceptionally or normally (EOF reached).
   *
   * @return true if the flow is finished
   */

  boolean isFinished();

  /**
   * Returns {@code s} if {@code s} is a {@code TubeSubscriber}, otherwise wraps it in a
   * {@code TubeSubscriber}. Using the wrapper is only appropriate in the case where
   * {@code dropSubscription} doesn't need to be implemented, and the {@code TubeSubscriber} is
   * subscribed only once.
   *
   * @param s
   *          a subscriber for reading.
   * @return a {@code TubeSubscriber}: either {@code s} if {@code s} is a {@code TubeSubscriber},
   *         otherwise a {@code TubeSubscriber} wrapper that delegates to {@code s}
   */

  static <T> TubeSubscriber<T> asTubeSubscriber(final Flow.Subscriber<? super T> s) {
    if (s instanceof TubeSubscriber) {
      return (TubeSubscriber<T>) s;
    }
    return new AbstractTubeSubscriber.TubeSubscriberWrapper<>(s);
  }

  /**
   * Returns {@code s} if {@code s} is a {@code  TubePublisher}, otherwise wraps it in a
   * {@code  TubePublisher}.
   *
   * @param p
   *          a publisher for writing.
   * @return a {@code TubePublisher}: either {@code s} if {@code s} is a {@code  TubePublisher},
   *         otherwise a {@code TubePublisher} wrapper that delegates to {@code s}
   */

  static <T> TubePublisher<T> asTubePublisher(final Flow.Publisher<T> p) {
    if (p instanceof TubePublisher) {
      return (TubePublisher<T>) p;
    }
    return new AbstractTubePublisher.TubePublisherWrapper<>(p);
  }

  /**
   * Convenience abstract class for {@code TubePublisher} implementations. It is not required that a
   * {@code TubePublisher} implementation extends this class.
   */

  static abstract class AbstractTubePublisher<T> implements TubePublisher<T> {

    static final class TubePublisherWrapper<T> extends AbstractTubePublisher<T> {

      final Flow.Publisher<T> delegate;

      public TubePublisherWrapper(final Flow.Publisher<T> delegate) {
        this.delegate = delegate;
      }

      @Override
      public void subscribe(final Flow.Subscriber<? super T> subscriber) {
        this.delegate.subscribe(subscriber);
      }

    }

  }

  /**
   * Convenience abstract class for {@code TubeSubscriber} implementations. It is not required that
   * a {@code TubeSubscriber} implementation extends this class.
   */

  static abstract class AbstractTubeSubscriber<T> implements TubeSubscriber<T> {

    static final class TubeSubscriberWrapper<T> extends AbstractTubeSubscriber<T> {

      final Flow.Subscriber<? super T> delegate;

      TubeSubscriberWrapper(final Flow.Subscriber<? super T> delegate) {
        this.delegate = delegate;
      }

      @Override
      public void dropSubscription() {
      }

      @Override
      public void onSubscribe(final Flow.Subscription subscription) {
        this.delegate.onSubscribe(subscription);
      }

      @Override
      public void onNext(final T item) {
        this.delegate.onNext(item);
      }

      @Override
      public void onError(final Throwable throwable) {
        this.delegate.onError(throwable);
      }

      @Override
      public void onComplete() {
        this.delegate.onComplete();
      }

    }

  }

}
