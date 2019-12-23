package io.rtcore.sip.signaling.txn;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.signaling.txn.StateMachineFactory;

class StateMachineFactoryTest {

  @Test
  void test() {

    StateMachineFactory.createMachine("INVITE");

  }

}
