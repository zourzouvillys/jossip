import io.rtcore.sip.channels.ManagedSipChannelProvider;
import io.rtcore.sip.channels.ManagedSipUdpSocketProvider;
import io.rtcore.sip.channels.SipNameResolver;

module io.rtcore.sip.channels {

  uses ManagedSipUdpSocketProvider;
  uses ManagedSipChannelProvider;
  uses SipNameResolver.Provider;

  exports io.rtcore.sip.channels;
  exports io.rtcore.sip.channels.utils;
  exports io.rtcore.sip.channels.dispatch;
  exports io.rtcore.sip.channels.handlers;
  exports io.rtcore.sip.channels.endpoint;

  requires transitive io.rtcore.sip.common;
  requires transitive io.rtcore.sip.parser;

  requires transitive org.reactivestreams;

  requires static org.eclipse.jdt.annotation;
  requires java.base;
  requires com.google.common;
  requires io.reactivex.rxjava3;
  requires static org.immutables.value.annotations;
  requires java.logging;
  requires com.github.akarnokd.rxjava3jdk9interop;

}
