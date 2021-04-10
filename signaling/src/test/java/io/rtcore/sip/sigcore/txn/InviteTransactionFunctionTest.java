package io.rtcore.sip.sigcore.txn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.processor.rfc3261.MutableSipRequest;
import io.rtcore.sip.message.uri.SipUri;
import io.rtcore.sip.sigcore.Address;
import io.rtcore.sip.sigcore.invoke.StateOperation;
import io.rtcore.sip.sigcore.invoke.StateOperationRecorder;
import io.rtcore.sip.sigcore.txn.InviteTransactionFunction.State;

class InviteTransactionFunctionTest {

  /**
   * 
   */

  @Test
  void test() {

    //
    List<StateOperation> out = new ArrayList<>();
    StateOperationRecorder recorder = new StateOperationRecorder(out);
    DefaultInviteClientTxnBehavior behavior = new DefaultInviteClientTxnBehavior(recorder);

    // the underlying event.
    RxSipFrame req =
      ImmutableRxSipFrame.of(
        new InetSocketAddress(1000),
        new InetSocketAddress(2000),
        MutableSipRequest.create(SipMethod.INVITE, SipUri.ANONYMOUS).build());

    //
    State state =
      InviteTransactionFunction.process(
        State.Calling,
        TransactionMessages.startTransaction(req, Address.of("sip", "transport", "udp:sip.server")),
        behavior);

    // set the state, with an expiry if possible.
    recorder.mergeState("state", state);

    // check the state is correct.
    assertEquals(State.Calling, state);

    // transmitting again should not result in anything happening.
    State state2 =
      InviteTransactionFunction.process(
        State.Calling,
        TransactionMessages.startTransaction(req, Address.of("sip", "transport", "udp:sip.server")),
        behavior);

    // the output state operations:
    out.forEach(System.err::println);

    //

  }

}
