package io.rtcore.sip.channels.netty.resolver;

import static org.reactivestreams.FlowAdapters.toSubscriber;

import java.util.concurrent.Flow.Subscriber;

import io.reactivex.rxjava3.core.Flowable;
import io.rtcore.sip.channels.SipNameResolver;

class StaticNameResolver implements SipNameResolver {

  private final Flowable<ResolutionResult> result;

  StaticNameResolver(final ResolutionResult result) {
    this.result = Flowable.just(result);
  }

  StaticNameResolver(final Flowable<ResolutionResult> result) {
    this.result = result;
  }

  @Override
  public void subscribe(final Subscriber<? super ResolutionResult> subscriber) {
    this.result.subscribe(toSubscriber(subscriber));
  }

}
