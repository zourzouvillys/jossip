package io.rtcore.sip.channels.dispatch;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Flow;
import java.util.concurrent.TimeUnit;

import org.reactivestreams.FlowAdapters;

import com.google.common.hash.Hashing;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableSubscriber;
import io.reactivex.rxjava3.processors.BehaviorProcessor;
import io.reactivex.rxjava3.processors.PublishProcessor;
import io.reactivex.rxjava3.processors.UnicastProcessor;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.SipResponse;

/**
 * provider which reliably transmits over a stateless flow.
 */

public final class SipTransactions {

  // all currently pending client transactions.
  private final Map<String, FlowableSubscriber<SipResponse>> pending = new HashMap<>();

  private final PublishProcessor<SipResponse> unknownResponses = PublishProcessor.create();

  public SipTransactions() {
  }

  /**
   * accept a new incoming response.
   * 
   * @param res
   * @return
   */

  public boolean accept(final SipResponse res) {

    final FlowableSubscriber<SipResponse> txn = this.pending.get(res.branchId().getValueWithoutCookie().get());

    if (txn == null) {
      this.unknownResponses.offer(res);
      return false;
    }

    //
    txn.onNext(res);
    return true;

  }

  /**
   * each transmission over a SIP transport requires a branch identifier to correlate the response
   * to the original request.
   *
   * this processor generates a branch identifier and adds it to the request. it then registers this
   * identifier as an active transaction before sending over the wire based on the retransmission
   * rules for the target.
   *
   * once a response is received, the retransmit source is terminated, and the original response is
   * provided.
   *
   */

  public Flow.Publisher<SipResponse> transmit(final SipRequest req, final Flow.Subscriber<SipRequest> tx) {

    final String key =
      Hashing.farmHashFingerprint64()
        .hashUnencodedChars(UUID.randomUUID().toString())
        .toString();

    // create state for this specific sip exchange, which allocates a unique key.
    // the key is valid for as long as there is a subscriber.

    final BehaviorProcessor<Void> stop = BehaviorProcessor.create();

    final Flowable<SipResponse> subscriber = this.createBranch(key);

    // we keep transmitting until a stop signal
    SipTransactions.transact(req)
      .takeUntil(stop)
      .subscribe(FlowAdapters.toSubscriber(tx));

    return FlowAdapters.toFlowPublisher(subscriber.doOnCancel(() -> stop.onComplete()));

  }

  /**
   * create a branch which will register on subscription, and remove itself on close. the subscriber
   * will receive all messages with this branch id for as long as there is a subscriber
   *
   * @param key
   * @return
   */

  private Flowable<SipResponse> createBranch(final String key) {
    final UnicastProcessor<SipResponse> responder = UnicastProcessor.create(1, () -> {
      this.pending.remove(key);
      System.err.printf("removed, now %,d\n", this.pending.size());
    });
    return responder
      .doOnCancel(responder::onComplete)
      .doOnSubscribe(sub -> {
        this.pending.put(key, responder);
        System.err.printf("added, now %,d\n", this.pending.size());
      });
  }

  /**
   * returns a publisher which will emit requests following the transaction retry timers rules for
   * the transport.
   */

  private static Flowable<SipRequest> transact(final SipRequest req) {
    return Flowable.just(req)
      .repeatWhen(retry -> retry
        .zipWith(Flowable.range(1, Integer.MAX_VALUE), (n, i) -> i)
        .flatMap(SipTransactions::nextTimer));
  }

  /**
   * given a retry count, returns the next transmission time.
   */

  private static Flowable<?> nextTimer(final int retryCount) {
    return Flowable.timer(Math.min(500 * retryCount, 4000), TimeUnit.MILLISECONDS);
  }

}
