
module io.rtcore.sip.channels {

  exports io.rtcore.sip.channels.proxy;
  
  requires io.rtcore.sip.channels.netty;

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
