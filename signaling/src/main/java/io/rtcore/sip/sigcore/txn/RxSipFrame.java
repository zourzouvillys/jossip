package io.rtcore.sip.sigcore.txn;

import java.net.SocketAddress;

import org.immutables.value.Value;

import io.rtcore.sip.message.message.SipMessage;

@Value.Immutable
public interface RxSipFrame extends RxFrame {

  @Override
  @Value.Parameter
  SocketAddress source();

  @Override
  @Value.Parameter
  SocketAddress target();

  @Value.Parameter
  SipMessage message();

}
