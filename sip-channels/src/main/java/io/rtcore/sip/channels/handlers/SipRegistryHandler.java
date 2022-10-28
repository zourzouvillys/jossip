package io.rtcore.sip.channels.handlers;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipServerExchange;
import io.rtcore.sip.channels.api.SipServerExchange.Listener;
import io.rtcore.sip.channels.api.SipServerExchangeHandler;
import io.rtcore.sip.channels.api.SipServerExchangeInterceptor;
import io.rtcore.sip.channels.interceptors.SipServerInterceptors;

public class SipRegistryHandler<ReqT, ResT> implements SipServerExchangeHandler<ReqT, ResT> {

  public interface Route<ReqT, ResT> {

    Optional<SipServerExchangeHandler<ReqT, ResT>> apply(ReqT request, SipAttributes attributes);

  }

  //
  private final List<Route<ReqT, ResT>> routes;
  private final SipServerExchangeHandler<ReqT, ResT> defaultHandler;

  public SipRegistryHandler(List<Route<ReqT, ResT>> routes, SipServerExchangeHandler<ReqT, ResT> defaultHandler) {
    this.routes = List.copyOf(routes);
    this.defaultHandler = defaultHandler;
  }

  @Override
  public Listener startExchange(SipServerExchange<ReqT, ResT> exchange, SipAttributes attributes) {

    for (Route<ReqT, ResT> route : routes) {

      SipServerExchangeHandler<ReqT, ResT> applicator = route.apply(exchange.request(), attributes).orElse(null);

      if (applicator != null) {
        return applicator.startExchange(exchange, attributes);
      }

    }

    return this.defaultHandler.startExchange(exchange, attributes);

  }

  public static <
      ReqT, ResT> Route<ReqT, ResT>
      createRoute(Predicate<ReqT> predicate, SipServerExchangeHandler<ReqT, ResT> handler, List<SipServerExchangeInterceptor<ReqT, ResT>> interceptors) {
    return createRoute(predicate, SipServerInterceptors.interceptedHandler(handler, interceptors));
  }

  public static <ReqT, ResT> Route<ReqT, ResT> createRoute(Predicate<ReqT> options, SipServerExchangeHandler<ReqT, ResT> handler) {
    return new Route<ReqT, ResT>() {
      @Override
      public Optional<SipServerExchangeHandler<ReqT, ResT>> apply(ReqT request, SipAttributes attributes) {
        if (options.test(request)) {
          return Optional.of(handler);
        }
        return Optional.empty();
      }
    };
  }

  public static <ReqT, ResT> Route<ReqT, ResT> createRoute(BiPredicate<ReqT, SipAttributes> options, SipServerExchangeHandler<ReqT, ResT> handler) {
    return new Route<ReqT, ResT>() {
      @Override
      public Optional<SipServerExchangeHandler<ReqT, ResT>> apply(ReqT request, SipAttributes attributes) {
        if (options.test(request, attributes)) {
          return Optional.of(handler);
        }
        return Optional.empty();
      }
    };
  }

}
