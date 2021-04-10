package io.rtcore.sip.sigcore.txn;

import io.rtcore.sip.sigcore.Address;
import io.rtcore.sip.sigcore.invoke.StateHandle;

public class DefaultInviteClientTxnBehavior extends DefaultNonInviteClientTxnBehavior implements InviteClientTxnBehavior {

  public DefaultInviteClientTxnBehavior(StateHandle operations) {
    super(operations);
  }

  @Override
  public void sendAck(RxSipFrame msg) {
    // todo: make an ACK + send it.
    this.operations.invoke(Address.of("sip", "transport", "udp:1.2.3.4:5060:4.5.6.7:5060"), TransportMessages.transmitMessage(msg.message()));
  }

}
