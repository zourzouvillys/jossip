package io.rtcore.sip.channels.netty.udp;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.channels.endpoint.SipEndpoint;

class SipEndpointTest {

  @Test
  void testBuilderNoOptions() {
    SipEndpoint.create();
  }

  @Test
  void test() {

    // final var endpoint =
    // SipEndpoint.builder()
    // .udp(new InetSocketAddress(0))
    // .requestHandler(FunctionServerCallHandler.staticResponse(SipStatusCodes.METHOD_NOT_ALLOWED))
    // .build();
    //
    // //
    // final var OPTIONS =
    // MutableSipRequest.create(SipMethod.OPTIONS)
    // .from(SipUri.ANONYMOUS)
    // .to(SipUri.ANONYMOUS)
    // .contact(SipUri.ANONYMOUS)
    // .callId("dwdwed")
    // .maxForwards(5)
    // .cseq(1, SipMethod.OPTIONS)
    // .via(ViaProtocol.UDP, HostAndPort.fromString("localhost"), "xyz", true)
    // .build();

    //
    // FlowInterop.fromFlowPublisher(endpoint.exchange(OPTIONS,
    // SipCallOptions.of().withAuthority(Host.fromString("example.com"))))
    // .blockingForEach(System.err::println);

    // create SIP OPTIONS request.
    // endpoint.exchange(options);

    // wait until done.
    // fromPublisher(toPublisher(endpoint.start())).blockingForEach(System.err::println);

  }

}
