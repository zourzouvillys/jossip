import io.rtcore.sip.channels.internal.ManagedSipChannelProvider;
import io.rtcore.sip.channels.internal.ManagedSipUdpSocketProvider;
import io.rtcore.sip.channels.internal.SipNameResolver;
import io.rtcore.sip.channels.netty.NettySipChannelProvider;
import io.rtcore.sip.channels.netty.resolver.NettySipNameResolverProvider;
import io.rtcore.sip.channels.netty.udp.NettyUdpSocketProvider;

module io.rtcore.sip.channels.netty {

  //
  provides ManagedSipUdpSocketProvider with NettyUdpSocketProvider;
  provides ManagedSipChannelProvider with NettySipChannelProvider;
  provides SipNameResolver.Provider with NettySipNameResolverProvider;

  requires transitive io.rtcore.sip.channels;

  exports io.rtcore.sip.channels.netty.codec;
  exports io.rtcore.sip.channels.netty.udp;
  exports io.rtcore.sip.channels.netty.resolver;
  exports io.rtcore.sip.channels.netty;

  requires static com.google.auto.service;

  requires com.google.common;
  requires io.reactivex.rxjava3;
  requires io.rtcore.sip.parser;
  requires org.reactivestreams;

  requires transitive io.netty.buffer;
  requires transitive io.netty.codec;
  requires transitive io.netty.common;
  requires transitive io.netty.transport;
  requires io.netty.resolver.dns;
  requires io.netty.resolver;
  requires static java.compiler;

  
  requires static org.eclipse.jdt.annotation;
  requires java.base;
  requires static org.immutables.value.annotations;
  requires java.logging;
  requires io.rtcore.sip.common;
  requires io.netty.handler.proxy;
  requires io.netty.handler;
  requires org.slf4j;
  
}
