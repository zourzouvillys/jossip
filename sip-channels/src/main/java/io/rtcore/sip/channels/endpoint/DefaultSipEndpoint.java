package io.rtcore.sip.channels.endpoint;

import java.util.concurrent.Flow.Publisher;

import org.reactivestreams.FlowAdapters;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.rtcore.sip.channels.SipCallOptions;
import io.rtcore.sip.channels.SipClientCall;
import io.rtcore.sip.channels.SipWireProducer;
import io.rtcore.sip.message.message.SipRequest;

class DefaultSipEndpoint implements ManagedSipEndpoint {

  private final SipEndpointConfig config;
  private final CompositeDisposable resources = new CompositeDisposable();

  public DefaultSipEndpoint(final SipEndpointConfig config) {
    this.config = config;
  }

  @Override
  public Publisher<State> start() {

    this.resources.add(Flowable.fromPublisher(FlowAdapters.toPublisher(this.config.socket()))
      .map(SipWireProducer::next)
      .forEach(System.err::println));

    return FlowAdapters.toFlowPublisher(Flowable.just(State.NEW, State.STARTING, State.RUNNING).concatWith(Completable.never()));

  }

  @Override
  public SipClientCall exchange(final SipRequest sender, final SipCallOptions options) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipChannel.exchange invoked.");
  }

}
