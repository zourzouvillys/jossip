package io.rtcore.gateway.engine.sip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import io.rtcore.gateway.udp.SipDatagramSocket;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.frame.SipRequestFrame;

class NetworkSegmentTest {

  @Test
  void test() throws InterruptedException, ExecutionException, TimeoutException {

    final CountDownLatch latch = new CountDownLatch(1);

    final NetworkSegment segment = new NetworkSegment(ctx -> {
      System.err.println("got request" + ctx);
      latch.countDown();
    });

    final SipDatagramSocket udp =
      SipDatagramSocket.create(b -> b
        .bindAddress(new InetSocketAddress(Inet6Address.getLoopbackAddress(), 0))
        .messageHandler(segment.new DatagramMessageReceiverAdapter()))
        .bindNow();

    // check our returned address is equal to the one we provided.
    assertEquals(Inet6Address.getLoopbackAddress(), udp.localSocketAddress().getAddress());

    // now transmit a single test packet.
    udp
      .transmit(udp.localSocketAddress(),
        SipRequestFrame.of(SipMethods.OPTIONS, "sip:test@invalid")
          .withHeaderLines(StandardSipHeaders.VIA.ofLine("SIP/2.0/UDP 1.2.3.4:5555;branch=z9hG4bKAAA")));

    // now transmit a single test packet.
    udp
      .transmit(udp.localSocketAddress(),
        SipRequestFrame.of(SipMethods.OPTIONS, "sip:test@invalid")
          .withHeaderLines(StandardSipHeaders.VIA.ofLine("SIP/2.0/UDP 1.2.3.4:5555;branch=z9hG4bKAAA")));

    assertTrue(latch.await(1, TimeUnit.SECONDS));

    assertEquals(0, segment.serverStore().absorbtionSize());

  }

}
