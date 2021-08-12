package io.rtcore.sip.channels.endpoint;

import io.rtcore.sip.channels.SipServerCallHandler;
import io.rtcore.sip.channels.SipUdpSocket;

public interface SipEndpointBuilder extends SipSdkBuilder<SipEndpointBuilder, SipEndpoint> {

  /**
   * the sockets we will use for sending and receiving.
   */

  SipEndpointBuilder socket(SipUdpSocket socket);

  /**
   *
   */

  SipEndpointBuilder requestHandler(SipServerCallHandler handler);

  /**
   * build and return the newly created instance. it will need to be started.
   */

  @Override
  ManagedSipEndpoint build();

}
