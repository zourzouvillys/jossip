package io.rtcore.resolver.dns;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.common.base.Joiner;

public final class DnsOverHttpsResolver {

  private final HttpClient client;
  private final URI endpoint;

  public DnsOverHttpsResolver(HttpClient client, URI endpoint) {
    this.client = client;
    this.endpoint = endpoint;
  }

  public CompletableFuture<DnsResponse> query(String domainName, DnsRecordType recordType) {
    return query(domainName, recordType, Optional.empty());
  }

  public CompletableFuture<DnsResponse> query(String domainName, DnsRecordType recordType, ClientSubnet clientSubnet) {
    return query(domainName, recordType, Optional.ofNullable(clientSubnet));
  }

  public CompletableFuture<DnsResponse> query(String domainName, DnsRecordType recordType, Optional<ClientSubnet> clientSubnet) {

    Map<String, String> params = new HashMap<>();

    params.put("name", domainName);
    params.put("type", recordType.name());
    params.put("do", "true");
    params.put("ct", "application/x-javascript");

    clientSubnet.ifPresent(value -> params.put("edns_client_subnet", value.toString()));

    String queryString = Joiner.on("&").withKeyValueSeparator("=").join(params);

    HttpRequest request;
    try {
      request =
        HttpRequest
          .newBuilder(new URI(endpoint.getScheme(), endpoint.getUserInfo(), endpoint.getHost(), endpoint.getPort(), endpoint.getPath(), queryString, null))
          .GET()
          .header("accept", "application/dns-json")
          .build();
    }
    catch (URISyntaxException e) {
      throw new IllegalArgumentException("invalid URI");
    }

    return this.client
      .sendAsync(request, new JsonBodyHandler<DnsResponse>(DnsResponse.class))
      .thenApply(res -> res.body().get());

  }

}
