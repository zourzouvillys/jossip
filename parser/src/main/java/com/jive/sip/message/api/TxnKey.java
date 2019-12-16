package com.jive.sip.message.api;

import org.immutables.value.Value;

import com.google.common.net.HostAndPort;

@Value.Immutable
public abstract class TxnKey {

  /**
   * the host and port from the top Via sent-by.
   */

  @Value.Parameter
  abstract HostAndPort sentBy();

  /**
   * SIP method, except for ACKs, where the method is INVITE.
   */

  @Value.Parameter
  abstract SipMethod method();

  /**
   * the top branch value, excluding magic cookie.
   */

  @Value.Parameter
  abstract String branchId();

  @Override
  public String toString() {
    return String.format("TxnKey(%s,%s,%s)", this.sentBy(), this.method().getMethod().substring(0, 3), this.branchId());
  }

  public TxnKey withMethod(SipMethod method) {
    if (method().equals(method)) {
      return this;
    }
    return ImmutableTxnKey.of(this.sentBy(), method, this.branchId());
  }

  public static TxnKey forMessage(SipMessage msg) {
    return ImmutableTxnKey
      .of(
        msg.topVia().orElseThrow().getSentBy(),
        methodFor(msg),
        msg.getBranchId().getValueWithoutCookie());
  }

  public static SipMethod methodFor(SipMessage msg) {
    if (msg instanceof SipRequest) {
      SipMethod method = ((SipRequest) msg).getMethod();
      if (method.isAck()) {
        return SipMethod.INVITE;
      }
    }
    return msg.getCSeq().getMethod();
  }

}
