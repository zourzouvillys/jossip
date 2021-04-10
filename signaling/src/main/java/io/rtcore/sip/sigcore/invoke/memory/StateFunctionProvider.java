package io.rtcore.sip.sigcore.invoke.memory;

public interface StateFunctionProvider {

  StateFunction provide(String namespace, String type);

}
