package io.rtcore.sip.sigcore.txn;

import org.immutables.value.Value;

import io.rtcore.sip.message.message.SipMessage;
import io.rtcore.sip.sigcore.txn.StateMachine.Event;

@Value.Enclosing
public interface TransportMessages {

  @Value.Immutable
  public interface TransmitMessage extends Event<SipMessage> {

    @Override
    @Value.Parameter
    SipMessage payload();

  }

  static TransmitMessage transmitMessage(SipMessage message) {
    return ImmutableTransportMessages.TransmitMessage.of(message);
  }

}
