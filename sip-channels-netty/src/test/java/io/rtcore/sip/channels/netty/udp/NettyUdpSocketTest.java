package io.rtcore.sip.channels.netty.udp;

import java.net.InetSocketAddress;
import java.util.UUID;

import org.junit.jupiter.api.Test;

import com.google.common.net.InetAddresses;

import io.netty.channel.nio.NioEventLoopGroup;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.connection.SipConnections;
import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.StandardSipHeaders;

class NettyUdpSocketTest {

  @Test
  void test() throws InterruptedException {

    NioEventLoopGroup sharedLoop = new NioEventLoopGroup();

    NettyUdpSocket socket = NettyUdpSocket.create(sharedLoop, new InetSocketAddress(0), pkt -> System.err.println("req"));

    for (int i = 0; i < 1; ++i) {

      SipClientExchange exchange =
        socket.exchange(
          new InetSocketAddress(InetAddresses.forString("35.83.240.193"), 5060),
          SipRequestFrame.of(SipMethods.OPTIONS, "sip:35.83.240.193:5060")
            .withHeaderLines(
              StandardSipHeaders.FROM.ofLine("<sip:67.183.73.143:5060>;tag=x"),
              StandardSipHeaders.TO.ofLine("<sip:35.83.240.193:5060>"),
              StandardSipHeaders.CALL_ID.ofLine(UUID.randomUUID().toString()),
              StandardSipHeaders.CSEQ.ofLine("1 OPTIONS")),
          SipAttributes.newBuilder()
            .set(SipConnections.ATTR_SENT_BY, HostPort.fromHost("testing"))
            .build());

      exchange
        .responses()
        .subscribe(
          res -> {
            System.err.println(res.response().initialLine());
            res.response().headerLines().forEach(hdr -> System.err.printf("[%s]: [%s]\n", hdr.headerName(), hdr.headerValues()));
          },
          err -> {
            err.printStackTrace();
          },
          () -> {
            System.err.println("DONE");
          });

    }

    // exchange.responses().forEach(System.err::println);

    Thread.sleep(10_000);

  }

}
