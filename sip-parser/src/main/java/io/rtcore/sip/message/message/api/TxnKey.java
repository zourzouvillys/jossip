package io.rtcore.sip.message.message.api;

import org.immutables.value.Value;

import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.message.message.SipRequest;

@Value.Immutable
@Value.Style(
  jdkOnly = true,
  allowedClasspathAnnotations = { Override.class })
public abstract class TxnKey {

  /**
   * the host and port from the top Via sent-by.
   */

  @Value.Parameter
  abstract HostPort sentBy();

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

  public TxnKey withMethod(final SipMethod method) {
    if (this.method().equals(method)) {
      return this;
    }
    return ImmutableTxnKey.of(this.sentBy(), method, this.branchId());
  }

  public static TxnKey forMessage(final SipMessage msg) {
    return ImmutableTxnKey
        .of(
          msg.topVia().orElseThrow().sentBy(),
          methodFor(msg),
          msg.branchId().getValueWithoutCookie().orElseThrow());
  }

  public static SipMethod methodFor(final SipMessage msg) {
    if (msg instanceof SipRequest) {
      final SipMethod method = ((SipRequest) msg).method();
      if (method.isAck()) {
        return SipMethod.INVITE;
      }
      return method;
    }
    return msg.cseq().method();
  }

}
