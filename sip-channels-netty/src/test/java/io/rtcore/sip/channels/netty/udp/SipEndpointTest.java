package io.rtcore.sip.channels.netty.udp;

import java.net.InetSocketAddress;

import org.junit.jupiter.api.Test;

import com.google.common.net.HostAndPort;

import hu.akarnokd.rxjava3.jdk9interop.FlowInterop;
import io.rtcore.sip.channels.SipCallOptions;
import io.rtcore.sip.channels.endpoint.SipEndpoint;
import io.rtcore.sip.channels.handlers.FunctionServerCallHandler;
import io.rtcore.sip.common.Host;
import io.rtcore.sip.message.message.SipResponseStatus;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.ViaProtocol;
import io.rtcore.sip.message.processor.rfc3261.MutableSipRequest;
import io.rtcore.sip.message.processor.rfc3261.MutableSipResponse;
import io.rtcore.sip.message.uri.SipUri;

class SipEndpointTest {

  @Test
  void testBuilderNoOptions() {
    SipEndpoint.create();
  }

  @Test
  void test() {

    final var endpoint =
      SipEndpoint.builder()
        .udp(new InetSocketAddress(0))
        .requestHandler(FunctionServerCallHandler.create(req -> MutableSipResponse.createResponse(req, SipResponseStatus.METHOD_NOT_ALLOWED).build()))
        .build();

    //
    final var OPTIONS =
      MutableSipRequest.create(SipMethod.OPTIONS)
        .from(SipUri.ANONYMOUS)
        .to(SipUri.ANONYMOUS)
        .contact(SipUri.ANONYMOUS)
        .callId("dwdwed")
        .maxForwards(5)
        .cseq(1, SipMethod.OPTIONS)
        .via(ViaProtocol.UDP, HostAndPort.fromString("localhost"), "xyz", true)
        .build();

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
