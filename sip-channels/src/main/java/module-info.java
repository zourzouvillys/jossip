import io.rtcore.sip.channels.ManagedSipChannelProvider;
import io.rtcore.sip.channels.ManagedSipUdpSocketProvider;

module io.rtcore.sip.channels {

  uses ManagedSipUdpSocketProvider;
  uses ManagedSipChannelProvider;

  exports io.rtcore.sip.channels;
  exports io.rtcore.sip.channels.dispatch;

  requires transitive io.rtcore.sip.common;
  requires transitive io.rtcore.sip.parser;

  requires transitive org.reactivestreams;

  requires static org.eclipse.jdt.annotation;
  requires java.base;
  requires com.google.common;
  requires io.reactivex.rxjava3;
  requires org.immutables.value.annotations;
  requires java.logging;

}
