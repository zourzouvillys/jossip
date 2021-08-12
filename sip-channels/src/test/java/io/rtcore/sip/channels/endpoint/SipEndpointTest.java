package io.rtcore.sip.channels.endpoint;

import static io.reactivex.rxjava3.core.Flowable.fromPublisher;
import static org.reactivestreams.FlowAdapters.toPublisher;

import java.net.InetSocketAddress;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.channels.SipChannels;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.processor.rfc3261.MutableSipRequest;

class SipEndpointTest {

  @Test
  void testBuilderNoOptions() {
    SipEndpoint.create();
  }

  @Test
  void test() {

    final ManagedSipEndpoint endpoint =
        SipEndpoint.builder()
        .socket(SipChannels.newUdpSocketBuilder().bindNow(new InetSocketAddress(5060)))
        .build();

    // create SIP OPTIONS request.
    endpoint.exchange(MutableSipRequest.create(SipMethod.OPTIONS).build());

    // wait until done.
    fromPublisher(toPublisher(endpoint.start())).blockingForEach(System.err::println);

  }

}
