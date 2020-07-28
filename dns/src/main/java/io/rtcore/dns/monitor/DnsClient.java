package io.rtcore.dns.monitor;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import io.reactivex.rxjava3.core.Maybe;
import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.ConnectionSpec;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

// related:
// https://research.nccgroup.com/2020/03/30/impact-of-dns-over-https-doh-on-dns-rebinding-attacks/
public class DnsClient {

  private final ImmutableList<String> endpoints;
  private OkHttpClient client;
  private ObjectReader reader;

  public enum DnsRecordType {
    A,
    AAAA,
    SRV,
    NAPTR,
  }

  public DnsClient() {
    // default, well known.
    this(
      Arrays.asList(
        "https://dns.google/resolve",
        "https://cloudflare-dns.com/dns-query"));
  }

  public DnsClient(List<String> endpoints) {

    ConnectionPool connectionPool = new ConnectionPool(2, 15, TimeUnit.SECONDS);

    Cache cache = new Cache(Paths.get("/tmp/dns-cache").toFile(), 1024 * 1024 * 64);

    this.client =
      new OkHttpClient.Builder()
        .connectionPool(connectionPool)
        .addNetworkInterceptor(new HttpLoggingInterceptor())
        .connectTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .readTimeout(5, TimeUnit.SECONDS)
        .callTimeout(10, TimeUnit.SECONDS)
        .connectionSpecs(Arrays.asList(ConnectionSpec.RESTRICTED_TLS))
        .cache(cache)
        .build();

    this.endpoints = ImmutableList.copyOf(endpoints);

    this.reader = new ObjectMapper().readerFor(DnsAnswer.class);
  }

  public static class DnsRecord {

    public String name;
    public int type;
    public int TTL;
    public String data;

    @Override
    public String toString() {
      return String.format("%s (expires %s)", data, TTL);
    }

  }

  public static class DnsAnswer {

    public int Status;
    public Boolean TC;
    public Boolean RD;
    public Boolean RA;
    public Boolean AD;
    public Boolean CD;
    public List<ObjectNode> Question;
    public List<DnsRecord> Answer;
    public String Comment;

    // {"Status": 0,"TC": false,"RD": true,"RA": true,"AD": false,"CD": false,
    // "Question":[ {"name": "testing.pstn.twilio.com.","type": 1}],
    // "Answer":[
    // {"name": "testing.pstn.twilio.com.","type": 1,"TTL": 599,"data": "54.172.60.3"},
    // {"name": "testing.pstn.twilio.com.","type": 1,"TTL": 599,"data": "54.172.60.0"},
    // {"name": "testing.pstn.twilio.com.","type": 1,"TTL": 599,"data": "54.172.60.1"},
    // {"name": "testing.pstn.twilio.com.","type": 1,"TTL": 599,"data": "54.172.60.2"}
    // ],
    // "Comment": "Response from 208.80.124.2."}

    // [{"name":"us1.twilio.com","type":6,"TTL":300,"data":"ns-cloud-d1.googledomains.com.
    // cloud-dns-hostmaster.google.com. 1 21600 3600 259200 300"}]
    public List<ObjectNode> Authority;

    //
    public String edns_client_subnet;
    public JsonNode Additional;

  }

  /**
   * 
   */

  private Record convert(DnsRecord record) {

    switch (record.type) {
      case 0x0001: // A
      case 0x001c: // AAAA
        return AddrRecord.parse(record.data);
      case 0x0021: // SRV
        return SrvRecord.parse(record.data);
      case 0x0023: // NAPTR
        return NaptrRecord.parse(record.data);
      default:
        throw new IllegalArgumentException(String.format("unexpected: ", record));
    }

  }

  // res.Status
  // var errors = [
  // { "name": "NoError", "description": "No Error"}, // 0
  // { "name": "FormErr", "description": "Format Error"}, // 1
  // { "name": "ServFail", "description": "Server Failure"}, // 2
  // { "name": "NXDomain", "description": "Non-Existent Domain"}, // 3
  // { "name": "NotImp", "description": "Not Implemented"}, // 4
  // { "name": "Refused", "description": "Query Refused"}, // 5
  // { "name": "YXDomain", "description": "Name Exists when it should not"}, // 6
  // { "name": "YXRRSet", "description": "RR Set Exists when it should not"}, // 7
  // { "name": "NXRRSet", "description": "RR Set that should exist does not"}, // 8
  // { "name": "NotAuth", "description": "Not Authorized"} // 9
  // ];

  /**
   * @return
   */

  public Maybe<Set<Record>> query(String domainName, DnsRecordType recordType) {

    HttpUrl url =
      HttpUrl.get(this.endpoints.get(0))
        .newBuilder()
        .addQueryParameter("name", domainName.toLowerCase())
        .addQueryParameter("type", recordType.name())
        .build();

    Request request =
      new Request.Builder()
        .url(url)
        .header("User-Agent", "RTCore")
        .addHeader("Accept", "application/dns-json")
        .build();

    try (Response response = client.newCall(request).execute()) {
      // System.err.println(response);
      // System.err.println(response.header("date"));
      // System.err.println(response.header("expires"));
      // System.err.println(response.header("cache-control"));
      // System.err.println(response.header("content-type"));
      return Maybe
        .just(this.reader.readValue(response.body().byteStream()))
        .cast(DnsAnswer.class)
        .filter(e -> e.Answer != null)
        .map(e -> e.Answer.stream().map(this::convert).collect(ImmutableSet.toImmutableSet()));
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

}
