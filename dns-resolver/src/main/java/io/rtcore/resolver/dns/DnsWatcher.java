package io.rtcore.resolver.dns;

import java.math.RoundingMode;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.math.IntMath;
import com.google.common.net.InternetDomainName;

import io.reactivex.rxjava3.core.Flowable;

public class DnsWatcher {

  private static final int MIN_TTL = 15;
  private static final int MAX_TTL = 300;

  private final Map<SrvKey, WatchedName> services = new HashMap<>();
  private final Map<InternetDomainName, WatchedNameAddress> addresses = new HashMap<>();

  private final DnsClient resolver;

  public record ServiceId(String service, String protocol) {
  }

  public record SrvKey(ServiceId service, String name) {
  }

  public static final ServiceId SIP_TLS = new ServiceId("sips", "tcp");
  public static final ServiceId SIP_TCP = new ServiceId("sip", "tcp");
  public static final ServiceId SIP_UDP = new ServiceId("sip", "udp");

  private DnsWatcher(DnsClient resolver) {
    this.resolver = resolver;
  }

  /**
   * calculate how long until we refresh this value.
   */

  private static int waiter(int ttl) {
    return Math.min(MAX_TTL, Math.max(MIN_TTL, Math.min(ttl - MIN_TTL, IntMath.divide(ttl, 2, RoundingMode.UP))));
  }

  private Flowable<Set<SrvValue>> queryAsync(SrvKey watchKey) {
    return Flowable
      .fromCompletionStage(resolver.srv(watchKey.service.service, watchKey.service.protocol, watchKey.name))
      .<Set<SrvValue>>concatMap(
        i -> Flowable.concat(
          Flowable.just(i.entries()),
          Flowable.timer(waiter(i.ttl()), TimeUnit.SECONDS).ignoreElements().toFlowable()));
  }

  private Flowable<Set<SrvValue>> watchAsync(SrvKey watchKey) {
    return queryAsync(watchKey).concatWith(Flowable.defer(() -> watchAsync(watchKey)));
  }

  class WatchedName {

    private final AtomicInteger refcnt = new AtomicInteger();
    private final SrvKey watchKey;
    private final Flowable<Set<SrvValue>> value;

    WatchedName(SrvKey watchKey) {
      this.watchKey = watchKey;
      this.value = watchAsync(watchKey).distinctUntilChanged();
    }

    WatchedName ref() {
      refcnt.getAndIncrement();
      return this;
    }

    void unref() {
      if (refcnt.decrementAndGet() == 0) {
        System.err.println("removed subscriber");
        services.remove(watchKey);
      }
    }

  }

  private Flowable<Set<InetAddress>> queryAsyncAddress(InternetDomainName watchKey) {
    return Flowable
      .fromCompletionStage(resolver.a(watchKey.toString()))
      .<Set<InetAddress>>concatMap(
        i -> Flowable.concat(
          Flowable.just(i.entries()),
          Flowable.timer(waiter(i.ttl()), TimeUnit.SECONDS).ignoreElements().toFlowable()));
  }

  private Flowable<Set<InetAddress>> watchAsyncAddress(InternetDomainName watchKey) {
    return queryAsyncAddress(watchKey).concatWith(Flowable.defer(() -> watchAsyncAddress(watchKey)));
  }

  class WatchedNameAddress {

    private final AtomicInteger refcnt = new AtomicInteger();
    private final InternetDomainName watchKey;
    private final Flowable<Set<InetAddress>> value;

    WatchedNameAddress(InternetDomainName watchKey) {
      this.watchKey = watchKey;
      this.value = watchAsyncAddress(watchKey).distinctUntilChanged();
    }

    WatchedNameAddress ref() {
      refcnt.getAndIncrement();
      return this;
    }

    void unref() {
      if (refcnt.decrementAndGet() == 0) {
        addresses.remove(watchKey);
      }
    }

  }

  public Flowable<Set<SrvValue>> srv(ServiceId srv, String name) {

    srv = new ServiceId(srv.service.toLowerCase(), srv.protocol.toLowerCase());

    SrvKey watchKey = new SrvKey(srv, InternetDomainName.from(name).toString());

    return Flowable.using(
      () -> services.computeIfAbsent(watchKey, WatchedName::new).ref(),
      w -> w.value,
      w -> w.unref(),
      false);

  }

  public Flowable<Set<InetAddress>> address(String name) {

    var watchKey = InternetDomainName.from(name);

    return Flowable.using(
      () -> addresses.computeIfAbsent(watchKey, WatchedNameAddress::new).ref(),
      w -> w.value,
      w -> w.unref(),
      false);

  }

  public static DnsWatcher create(DnsClient resolver) {
    return new DnsWatcher(resolver);
  }

}
