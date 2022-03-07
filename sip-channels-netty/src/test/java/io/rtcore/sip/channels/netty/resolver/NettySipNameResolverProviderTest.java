package io.rtcore.sip.channels.netty.resolver;

import java.io.IOException;
import java.net.URI;

import org.junit.jupiter.api.Test;

import hu.akarnokd.rxjava3.jdk9interop.FlowInterop;
import io.rtcore.sip.channels.internal.SipNameResolver;

class NettySipNameResolverProviderTest {

  @Test
  void test() throws IOException {

    final NettySipNameResolverProvider resolver = new NettySipNameResolverProvider();

    FlowInterop.fromFlowPublisher(resolver.newNameResolver(URI.create("sip:example.invalid")))
    .flatMap(FlowInterop::fromFlowPublisher)
    .cast(SipNameResolver.Address.class)
    .flatMap(FlowInterop::fromFlowPublisher)
    .blockingForEach(System.err::println);

  }

}
