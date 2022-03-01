package io.rtcore.sip.message.message.api;

import java.util.Optional;

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
  public abstract HostPort sentBy();

  /**
   * The SIP method, except for ACKs, where the method is INVITE.
   */

  @Value.Parameter
  public abstract SipMethod method();

  /**
   * the top branch value, excluding the magic cookie.
   */

  @Value.Parameter
  public abstract String branchId();

  @Override
  public String toString() {
    return String.format("TxnKey(%s,%s,%s)", this.sentBy(), this.method().getMethod(), this.branchId());
  }

  public TxnKey withMethod(final SipMethod method) {
    if (this.method().equals(method)) {
      return this;
    }
    return ImmutableTxnKey.of(this.sentBy(), method, this.branchId());
  }

  public static Optional<TxnKey> tryForMessage(final SipMessage msg) {
    return msg.topVia().flatMap(via -> via.branchWithoutCookie().map(branchId -> ImmutableTxnKey.of(via.sentBy(), methodFor(msg), branchId)));
  }

  public static TxnKey forMessage(final SipMessage msg) {
    return tryForMessage(msg).orElseThrow();
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
