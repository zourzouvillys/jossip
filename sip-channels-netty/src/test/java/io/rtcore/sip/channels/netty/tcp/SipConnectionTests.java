package io.rtcore.sip.channels.netty.tcp;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import com.google.common.hash.Hashing;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.reactivex.rxjava3.annotations.NonNull;
import io.rtcore.sip.channels.connection.ImmutableSipRoute;
import io.rtcore.sip.channels.connection.SipClientExchange.Event;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipConnectionProvider;
import io.rtcore.sip.channels.connection.SipRequestFrame;
import io.rtcore.sip.channels.netty.codec.SipFrameUtils;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.common.iana.StandardSipTransportName;
import io.rtcore.sip.message.uri.SipUri;

class SipConnectionTests {

  @Test
  public void test() throws Throwable {

    SelfSignedCertificate cert = new SelfSignedCertificate("localhost");

    NioEventLoopGroup loop = new NioEventLoopGroup(1);

    try {

      SipServerDispatcher dispatcher = call -> {
        call.sendResponse(SipFrameUtils.createResponse(call.request(), SipStatusCodes.OK));
        return null;
      };

      // start TLS listener.
      SipTlsServer server =
        SipTlsServer.createDefault(
          loop,
          SipTlsUtils.createServer(cert.key(), cert.cert()),
          dispatcher,
          new InetSocketAddress(0));

      server.startAsync().awaitRunning();

      // pool for outgoing connections
      SipConnectionProvider provider =
        SipConnectionPool
          .createTlsPool(loop, SipTlsUtils.createClient());

      try {

        ImmutableSipRoute route =
          ImmutableSipRoute.builder()
            .transportProtocol(StandardSipTransportName.TLS)
            .addRemoteServerNames("localhost")
            .remoteAddress(server.localAddress())
            .build();

        SipConnection conn = provider.requestConnection(route);

        try {

          @NonNull
          List<Event> res1 =
            conn
              .exchange(createOptions(SipUri.create(server.localAddress()).uri(), 1))
              .responses()
              .toList()
              .blockingGet();

          assertThat(res1.size(), is(1));
          assertThat(res1.get(0).response().initialLine().code(), is(200));

        }
        finally {

          //
          conn.close();

        }
      }
      finally {

        server.stopAsync().awaitTerminated();

      }

    }
    finally {

      loop.shutdownGracefully().get();

    }

  }

  private static SipRequestFrame createOptions(URI ruri, long seq) {
    return SipRequestFrame.of(
      SipMethods.OPTIONS,
      ruri,
      List.of(
        StandardSipHeaders.MAX_FORWARDS.ofLine("0"),
        StandardSipHeaders.CALL_ID.ofLine(UUID.randomUUID().toString()),
        StandardSipHeaders.TO.ofLine("<" + ruri.toASCIIString() + ">"),
        StandardSipHeaders.FROM
          .ofLine("<sip:invalid.domain>;tag=" + Hashing.farmHashFingerprint64().hashLong(ThreadLocalRandom.current().nextLong()).toString().substring(0, 8)),
        StandardSipHeaders.CSEQ.ofLine(seq + " OPTIONS")
      //
      ));
  }

}
