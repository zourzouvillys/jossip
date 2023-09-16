package io.rtcore.sip.channels.interceptors;

import java.util.Optional;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.processors.UnicastProcessor;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.channels.errors.ProxyAuthenticationRequired;
import io.rtcore.sip.channels.interceptors.SipClientAuthInterceptor.Attempt;
import io.rtcore.sip.channels.interceptors.SipClientAuthInterceptor.Generator;
import io.rtcore.sip.frame.SipRequestFrame;

class MultiSipClientExchange implements SipClientExchange {

  private final UnicastProcessor<Event> events = UnicastProcessor.create(true);
  private SipClientExchange exchange;
  private Generator generator;

  public MultiSipClientExchange(SipClientExchange exchange, Generator generator) {

    this.exchange = exchange;
    this.generator = generator;

    exchange.responses()
      .doOnNext(e -> {
        if (e.response().initialLine().code() == 407) {
          throw new ProxyAuthenticationRequired(e.response());
        }
      })
      .onErrorResumeNext(t -> send(t, generator.next(t)))
      .subscribe(events);
  }

  private Flowable<Event> send(Throwable t, Optional<Attempt> request) {

    if (request.isEmpty()) {
      return Flowable.error(t);
    }

    Attempt next = request.get();

    this.exchange = next.next().exchange(next.request());

    return exchange.responses()
      .doOnNext(e -> {
        if (e.response().initialLine().code() == 407) {
          throw new ProxyAuthenticationRequired(e.response());
        }
      })
      .onErrorResumeNext(ex -> send(ex, generator.next(ex)));

  }

  @Override
  public SipRequestFrame request() {
    return this.exchange.request();
  }

  @Override
  public SipAttributes attributes() {
    return this.exchange.attributes();
  }

  @Override
  public Flowable<Event> responses() {
    return this.events;
  }

  @Override
  public boolean cancel() {
    // there is only ever a single exchange ongoing. so only need to cancel the last.
    return this.exchange.cancel();
  }

}
