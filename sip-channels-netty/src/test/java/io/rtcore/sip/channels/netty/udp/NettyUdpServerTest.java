package io.rtcore.sip.channels.netty.udp;

import java.net.SocketException;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;

import io.netty.channel.nio.NioEventLoopGroup;
import io.rtcore.sip.message.message.SipRequest;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.processor.rfc3261.MutableSipRequest;

class NettyUdpServerTest {

  private static final SipRequest options = MutableSipRequest.create(SipMethod.OPTIONS).build();

  @Test
  void test() throws InterruptedException, ExecutionException, TimeoutException, SocketException {

    final NioEventLoopGroup group = new NioEventLoopGroup(1);

    try {

      final TestUserAgent uac = new TestUserAgent(group);
      final TestUserAgent uas = new TestUserAgent(group);

      // uas.exchange(uas.localAddress(), options);

      uac.write(uas.localAddress(), options);

      // wait until we receive the request on the uas.
      Awaitility.await("uas").atMost(Duration.ofSeconds(5)).until(uas.packets()::size, m -> m == 1);

      // FlowAdapters.toSubscriber(uac.writer(uas.localAddress()));

      // Flowable.just().subscribe();

      // uac.send(MutableSipRequest.create(SipMethod.OPTIONS).build(), uas.localAddress());
      //
      // uas.send(MutableSipRequest.create(SipMethod.OPTIONS).build(), uac.localAddress());
      // Awaitility.await("uac").atMost(Duration.ofSeconds(5)).until(uac.packets()::size, m -> m ==
      // 1);
      //
      // uas.send(MutableSipRequest.create(SipMethod.OPTIONS).build(), uac.localAddress());
      // Awaitility.await("uac").atMost(Duration.ofSeconds(5)).until(uac.packets()::size, m -> m ==
      // 2);
      //
      // uas.send(MutableSipRequest.create(SipMethod.OPTIONS).build(), uac.localAddress());
      // Awaitility.await("uac").atMost(Duration.ofSeconds(5)).until(uac.packets()::size, m -> m ==
      // 3);

      uas.close();
      uac.close();

    }
    finally {

      group.shutdownGracefully(0, 1, TimeUnit.SECONDS).get();

    }

  }

}
