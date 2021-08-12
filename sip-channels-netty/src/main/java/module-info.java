import io.rtcore.sip.channels.ManagedSipChannelProvider;
import io.rtcore.sip.channels.ManagedSipUdpSocketProvider;
import io.rtcore.sip.channels.netty.NettySipChannelProvider;
import io.rtcore.sip.channels.netty.udp.NettyUdpSocketProvider;

module io.rtcore.sip.channels.netty {

  provides ManagedSipUdpSocketProvider with NettyUdpSocketProvider;
  provides ManagedSipChannelProvider with NettySipChannelProvider;

  requires transitive io.rtcore.sip.channels;

  exports io.rtcore.sip.channels.netty.codec;
  exports io.rtcore.sip.channels.netty.udp;
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

}
