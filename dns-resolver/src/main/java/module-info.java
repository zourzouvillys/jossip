
module io.rtcore.sip.channels {

  exports io.rtcore.resolver.dns;

  requires transitive java.net.http;
  requires transitive org.reactivestreams;

  requires static org.eclipse.jdt.annotation;
  requires java.base;
  requires com.google.common;
  requires io.reactivex.rxjava3;
  requires static org.immutables.value.annotations;
  requires java.logging;
  requires com.github.akarnokd.rxjava3jdk9interop;
  requires com.fasterxml.jackson.databind;

}
