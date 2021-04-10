package io.rtcore.sip.sigcore.invoke.memory;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.sigcore.Address;
import io.rtcore.sip.sigcore.invoke.StateSignal;

class InMemoryStateProcessorTest {

  @Test
  void test() {

    StateFunctionRegistry functions = new StateFunctionRegistry();

    functions.register("test", "user", new TestFunction());

    InMemoryStateStore mem = new InMemoryStateStore();
    
    StateProcessor processor = new StateProcessor(mem, functions);

    processor.signal(
      Address.of("test", "user", "1"),
      StateSignal.of("RxMsg"));

  }

}
