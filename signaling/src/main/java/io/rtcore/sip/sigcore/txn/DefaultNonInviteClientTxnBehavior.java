package io.rtcore.sip.sigcore.txn;

import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.sigcore.Address;
import io.rtcore.sip.sigcore.invoke.StateHandle;
import io.rtcore.sip.sigcore.txn.TransactionMessages.StartTransaction;

public class DefaultNonInviteClientTxnBehavior implements NonInviteClientTxnBehavior {

  protected final StateHandle operations;

  public DefaultNonInviteClientTxnBehavior(StateHandle operations) {
    this.operations = operations;
  }

  @Override
  public void startTransaction(StartTransaction req) {
    SipMessage msg = operations.getState("request", SipMessage.class);
    if (msg != null) {
      return;
    }
    this.operations.mergeState("transport", req.transport());
    this.operations.mergeState("request", req.request().message());
    sendRequest();
  }

  @Override
  public void sendRequest() {
    // the request will call SEND on the transport instance which in turn results in the message
    // being sent to the remote peer, assuming there is sufficient capacity.
    SipMessage msg = operations.getState("request", SipMessage.class);
    this.operations.invoke(
      Address.of("sip", "transport", "udp:1.2.3.4:5060:4.5.6.7:5060"),
      TransportMessages.transmitMessage(msg));
  }

  /**
   * provide notification to the caller about the given message.
   */

  @Override
  public void notifyTU(RxSipFrame msg) {
    operations.invoke(Address.of("sip", "txnuser", "bobthebuilder"), msg);
  }

  /**
   * provide notification to the caller about the given error.
   */

  @Override
  public void notifyTU(Exception ex) {
    operations.invoke(Address.of("sip", "txnuser", "bobthebuilder"), ex);
  }

}
