package io.rtcore.resolver.dns;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;

class DnsClientTest {

  @Test
  void test() throws InterruptedException, ExecutionException {
    DnsClient client = DnsClient.createDOHClient("https://dns.google/resolve", ClientSubnet.forAddress("1.2.3.4"));
    CompletableFuture.allOf(client.a("google.com"), client.aaaa("google.com")).get();
  }

}
