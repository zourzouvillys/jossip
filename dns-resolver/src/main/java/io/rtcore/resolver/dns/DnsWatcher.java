package io.rtcore.resolver.dns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.net.InternetDomainName;

import io.reactivex.rxjava3.core.Flowable;

public class DnsWatcher {

  private final Map<SrvKey, WatchedName> services = new HashMap<>();

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

  private static int waiter(int ttl) {
    return 5; // Math.max(15, Math.min(ttl - 15, ttl / 2));
  }

  public Flowable<List<SrvValue>> queryAsync(SrvKey watchKey) {
    return Flowable
      .fromCompletionStage(resolver.srv(watchKey.service.service, watchKey.service.protocol, watchKey.name))
      .<List<SrvValue>>concatMap(
        i -> Flowable.concat(
          Flowable.just(i.entries()),
          Flowable.timer(waiter(i.ttl()), TimeUnit.SECONDS).ignoreElements().toFlowable()));
  }

  public Flowable<List<SrvValue>> watchAsync(SrvKey watchKey) {
    return queryAsync(watchKey).concatWith(Flowable.defer(() -> watchAsync(watchKey)));
  }

  public class WatchedName {

    private final AtomicInteger refcnt = new AtomicInteger();
    private final SrvKey watchKey;
    // private final FlowableProcessor<List<SrvValue>> source = PublishProcessor.create();
    private final Flowable<List<SrvValue>> value;

    public WatchedName(SrvKey watchKey) {
      this.watchKey = watchKey;
      this.value = watchAsync(watchKey).distinctUntilChanged();
    }

    public WatchedName ref() {
      refcnt.getAndIncrement();
      return this;
    }

    public void unref() {
      if (refcnt.decrementAndGet() == 0) {
        System.err.println("removed subscriber");
        services.remove(watchKey);
      }
    }

  }

  public Flowable<List<SrvValue>> srv(ServiceId srv, String name) {

    srv = new ServiceId(srv.service.toLowerCase(), srv.protocol.toLowerCase());

    SrvKey watchKey = new SrvKey(srv, InternetDomainName.from(name).toString());

    return Flowable.using(
      () -> services.computeIfAbsent(watchKey, WatchedName::new).ref(),
      w -> w.value,
      w -> w.unref(),
      false);

  }

  public static DnsWatcher create(DnsClient resolver) {
    return new DnsWatcher(resolver);
  }

}
