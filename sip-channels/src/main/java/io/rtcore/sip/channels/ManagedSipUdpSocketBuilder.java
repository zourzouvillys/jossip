package io.rtcore.sip.channels;

import java.net.InetSocketAddress;

public interface ManagedSipUdpSocketBuilder<T extends ManagedSipUdpSocketBuilder<T>> {

  /**
   * build this socket.
   */

  SipUdpSocket bindNow();

  SipUdpSocket bindNow(InetSocketAddress localAddress);

}
