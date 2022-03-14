package io.rtcore.sip.channels.endpoint;

import java.net.InetSocketAddress;

import io.rtcore.sip.channels.internal.SipChannels;
import io.rtcore.sip.channels.internal.SipUdpSocket;

public interface SipEndpointBuilder extends SipSdkBuilder<SipEndpointBuilder, SipEndpoint> {

  /**
   * the sockets we will use for sending and receiving.
   */

  SipEndpointBuilder socket(SipUdpSocket socket);

  /**
   *
   */

  //  SipEndpointBuilder requestHandler(SipServerCallHandler handler);

  /**
   * build and return the newly created instance. it will need to be started.
   */

  @Override
  ManagedSipEndpoint build();

  default SipEndpointBuilder udp(final InetSocketAddress listenAddress) {
    return this.socket(SipChannels.newUdpSocketBuilder().bindNow(listenAddress));
  }

}
