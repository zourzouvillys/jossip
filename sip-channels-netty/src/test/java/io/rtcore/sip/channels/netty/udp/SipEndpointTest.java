package io.rtcore.sip.channels.netty.udp;

import java.net.InetSocketAddress;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.channels.endpoint.SipEndpoint;
import io.rtcore.sip.channels.handlers.FunctionServerCallHandler;
import io.rtcore.sip.message.message.SipResponseStatus;
import io.rtcore.sip.message.processor.rfc3261.MutableSipResponse;

class SipEndpointTest {

  @Test
  void testBuilderNoOptions() {
    SipEndpoint.create();
  }

  @Test
  void test() {

    SipEndpoint.builder()
    .udp(new InetSocketAddress(0))
    .requestHandler(FunctionServerCallHandler.create(req -> MutableSipResponse.createResponse(req, SipResponseStatus.METHOD_NOT_ALLOWED).build()))
    .build();

    // create SIP OPTIONS request.
    // endpoint.exchange(MutableSipRequest.create(SipMethod.OPTIONS).build());

    // wait until done.
    // fromPublisher(toPublisher(endpoint.start())).blockingForEach(System.err::println);

  }

}
