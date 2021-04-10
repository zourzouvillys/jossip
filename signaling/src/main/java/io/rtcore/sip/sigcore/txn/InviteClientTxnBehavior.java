package io.rtcore.sip.sigcore.txn;

public interface InviteClientTxnBehavior extends NonInviteClientTxnBehavior {

  /**
   * sends an ack to the specified message by generating a suitable one and then transmitting to the
   * exact same source as the response was received from.
   */

  void sendAck(RxSipFrame msg);

  // some defaults which chain requests:

  // send ACK and then to TU.
  default void sendAckAndNotifyTU(RxSipFrame msg) {
    sendAck(msg);
    notifyTU(msg);
  }

}
