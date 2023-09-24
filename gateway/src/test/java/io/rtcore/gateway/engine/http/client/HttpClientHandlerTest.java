package io.rtcore.gateway.engine.http.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import io.rtcore.gateway.engine.http.server.HttpSipServer;

class HttpClientHandlerTest {

  @Test
  void test() throws InterruptedException, ExecutionException, TimeoutException {

    final HttpSipServer http = new HttpSipServer(new FakeExternalSipServer());

    http.startAsync().awaitRunning(5, TimeUnit.SECONDS);

    final HttpClientHandler client = new HttpClientHandler(HttpClient.newHttpClient());

    final String url = http.url("test123");

    final HttpResponse<Void> res = client.publish(URI.create(url), "{}").get();

    final CountDownLatch l = new CountDownLatch(1);

    System.err.println(res);

    l.await(5, TimeUnit.SECONDS);

    http.stopAsync().awaitTerminated(5, TimeUnit.SECONDS);

  }

}
