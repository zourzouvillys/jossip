package io.rtcore.gateway.udp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.Inet6Address;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.netty.codec.udp.SipDatagramPacket;

class SipDatagramSocketTest {

  @Test
  void test() throws InterruptedException, ExecutionException, TimeoutException {

    final LinkedTransferQueue<SipDatagramPacket> queue = new LinkedTransferQueue<>();

    final SipDatagramSocket udp =
      SipDatagramSocket.create(b -> b
        .bindAddress(new InetSocketAddress(Inet6Address.getLoopbackAddress(), 0))
        .messageHandler((socket, pkt) -> queue.put(pkt))).bindNow();

    // check our returned address is equal to the one we provided.
    assertEquals(Inet6Address.getLoopbackAddress(), udp.localSocketAddress().getAddress());

    // now transmit a single test packet.
    udp.transmit(udp.localSocketAddress(), SipRequestFrame.of(SipMethods.OPTIONS, "sip:test@invalid")).get(1, TimeUnit.SECONDS);

    // and ensure we receive it.
    final SipDatagramPacket recv = queue.poll(1, TimeUnit.SECONDS);

    System.err.println(recv);

  }

}
