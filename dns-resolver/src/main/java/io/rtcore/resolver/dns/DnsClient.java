package io.rtcore.resolver.dns;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;

public final class DnsClient {

  private final DnsOverHttpsResolver resolver;
  private final Optional<ClientSubnet> clientSubnet;

  private DnsClient(DnsOverHttpsResolver resolver, Optional<ClientSubnet> clientSubnet) {
    this.resolver = resolver;
    this.clientSubnet = clientSubnet;
  }

  public CompletableFuture<DnsRecord<SrvValue>> srv(String service, String protocol, String name) {
    String fqdn = String.format("_%s._%s.%s", service, protocol, InternetDomainName.from(name).toString());
    return this.resolver.query(fqdn, DnsRecordType.SRV, clientSubnet).thenApply(DnsClient::toSrvValues);
  }

  public CompletableFuture<DnsRecord<NaptrValue>> naptr(String fqdn) {
    return this.resolver.query(fqdn, DnsRecordType.NAPTR, clientSubnet).thenApply(DnsClient::toNAPTRValues);
  }

  public CompletableFuture<DnsRecord<InetAddress>> a(String fqdn) {
    return this.resolver.query(fqdn, DnsRecordType.A, clientSubnet).thenApply(DnsClient::toAValues);
  }

  public CompletableFuture<DnsRecord<InetAddress>> aaaa(String fqdn) {
    return this.resolver.query(fqdn, DnsRecordType.AAAA, clientSubnet).thenApply(DnsClient::toAAAAValue);
  }

  private static <T> DnsRecord<T> toRecord(DnsResponse res, DnsRecordType type, Function<DnsAnswerRecord, T> parser) {
    return res.answer()
      .stream()
      .filter(e -> e.type() == type)
      .map(e -> Maps.immutableEntry(e.ttl(), parser.apply(e)))
      .collect(Collectors.collectingAndThen(
        Collectors.toUnmodifiableList(),
        entries -> ImmutableDnsRecord.<T>of(
          entries.stream().mapToInt(e -> e.getKey()).min().orElse(0),
          Lists.transform(entries, Map.Entry::getValue))));
  }

  private static DnsRecord<SrvValue> toSrvValues(DnsResponse res) {
    return toRecord(res, DnsRecordType.SRV, e -> SrvValue.parse(e.data()));
  }

  private static DnsRecord<NaptrValue> toNAPTRValues(DnsResponse res) {
    return toRecord(res, DnsRecordType.NAPTR, e -> NaptrValue.parse(e.data()));
  }

  private static DnsRecord<InetAddress> toAValues(DnsResponse res) {
    return toRecord(res, DnsRecordType.A, DnsClient::toAddress4);
  }

  private static DnsRecord<InetAddress> toAAAAValue(DnsResponse res) {
    return toRecord(res, DnsRecordType.AAAA, DnsClient::toAddress6);
  }

  private static InetAddress toAddress4(DnsAnswerRecord record) {
    try {
      return Inet4Address.getByAddress(
        InternetDomainName.from(record.name()).toString(),
        InetAddresses.forString(record.data()).getAddress());
    }
    catch (UnknownHostException e1) {
      throw new RuntimeException(e1);
    }
  }

  private static InetAddress toAddress6(DnsAnswerRecord record) {
    try {
      return Inet6Address.getByAddress(
        InternetDomainName.from(record.name()).toString(),
        InetAddresses.forString(record.data()).getAddress());
    }
    catch (UnknownHostException e1) {
      throw new RuntimeException(e1);
    }
  }

  public static DnsClient createDOHClient(String url) {
    HttpClient client = HttpClient.newBuilder().build();
    DnsOverHttpsResolver resolver = new DnsOverHttpsResolver(client, URI.create(url));
    return new DnsClient(resolver, Optional.empty());
  }

  public static DnsClient createDOHClient(String url, ClientSubnet clientSubnet) {
    HttpClient client = HttpClient.newBuilder().build();
    DnsOverHttpsResolver resolver = new DnsOverHttpsResolver(client, URI.create(url));
    return new DnsClient(resolver, Optional.of(clientSubnet));
  }

}
