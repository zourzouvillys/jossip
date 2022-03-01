package io.rtcore.sip.channels.endpoint;

import java.net.InetSocketAddress;
import java.util.concurrent.Flow.Publisher;

import org.reactivestreams.FlowAdapters;

import hu.akarnokd.rxjava3.jdk9interop.FlowInterop;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.rtcore.sip.channels.SipCallOptions;
import io.rtcore.sip.channels.SipClientCall;
import io.rtcore.sip.channels.SipNameResolver;
import io.rtcore.sip.channels.SipNameResolver.Address;
import io.rtcore.sip.channels.SipWireProducer;
import io.rtcore.sip.common.Host;
import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.processor.uri.SipUriExtractor;

class DefaultSipEndpoint implements ManagedSipEndpoint {

  private final SipEndpointConfig config;
  private final CompositeDisposable resources = new CompositeDisposable();

  public DefaultSipEndpoint(final SipEndpointConfig config) {
    this.config = config;
  }

  @Override
  public Publisher<State> start() {

    this.resources.add(
      FlowInterop.fromFlowPublisher(this.config.socket())
      .map(SipWireProducer::next)
      .forEach(System.err::println));

    return FlowAdapters.toFlowPublisher(Flowable.just(State.NEW, State.STARTING, State.RUNNING).concatWith(Completable.never()));

  }

  /**
   * start a client exchange, sending the request to a server for handling.
   */

  @Override
  public SipClientCall exchange(final SipRequest request, final SipCallOptions options) {

    // the target address which will be used for calculating the correct outgoing destination.
    final Host authority = options.authority().orElseGet(() -> calculateAuthority(request));

    // we now use the name resolver to generate a series of

    final InetSocketAddress target =
        FlowInterop.fromFlowPublisher(SipNameResolver.newNameResolver(HostPort.of(authority)))
        .flatMap(FlowInterop::fromFlowPublisher)
        .cast(Address.class)
        .flatMap(FlowInterop::fromFlowPublisher)
        .cast(InetSocketAddress.class)
        .blockingFirst();

    System.err.println("sending to target: " + target);

    this.config.socket().send(target, FlowAdapters.toFlowPublisher(Flowable.just(request)));

    // TODO Auto-generated method stub
    // throw new UnsupportedOperationException("Unimplemented Method: SipChannel.exchange
    // invoked.");
    return new DefaultSipClientCall();

  }

  private static final Host calculateAuthority(final SipRequest sender) {
    if (sender.route().isEmpty()) {
      return sender.uri(SipUriExtractor.getInstance()).host();
    }
    return sender.route().get(0).address().apply(SipUriExtractor.getInstance()).host();

  }

}
