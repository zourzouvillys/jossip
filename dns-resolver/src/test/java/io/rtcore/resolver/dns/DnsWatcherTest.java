package io.rtcore.resolver.dns;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class DnsWatcherTest {

  @Test
  void test() {

    DnsClient client = DnsClient.createDOHClient("https://dns.google/resolve", ClientSubnet.forAddress("1.2.3.4"));

    DnsWatcher watcher = DnsWatcher.create(client);

    System.err.println(Instant.now());

    watcher.address("google.com")
      .take(2)
      .doOnNext(rec -> System.err.println(Instant.now() + ": " + rec))
      .doOnError(err -> System.err.println(Instant.now() + ": " + err))
      .doOnComplete(() -> System.err.print(Instant.now() + ": " + "DONE"))
      .blockingSubscribe();

    System.err.println(Instant.now());

  }

}
