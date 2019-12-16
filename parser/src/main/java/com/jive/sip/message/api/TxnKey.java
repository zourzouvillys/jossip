package com.jive.sip.message.api;

import org.immutables.value.Value;

import com.google.common.net.HostAndPort;

@Value.Immutable
public interface TxnKey {

  @Value.Parameter
  HostAndPort sentBy();

  @Value.Parameter
  SipMethod methodKey();

  @Value.Parameter
  String branchId();

  static TxnKey forMessage(SipMessage msg) {
    return ImmutableTxnKey
      .of(
        msg.getVias().get(0).getSentBy(),
        methodFor(msg),
        msg.getBranchId().getValueWithoutCookie());
  }

  static SipMethod methodFor(SipMessage msg) {
    if (msg instanceof SipRequest) {
      SipMethod method = ((SipRequest) msg).getMethod();
      if (method.isAck()) {
        return SipMethod.INVITE;
      }
    }
    return msg.getCSeq().getMethod();
  }

}
