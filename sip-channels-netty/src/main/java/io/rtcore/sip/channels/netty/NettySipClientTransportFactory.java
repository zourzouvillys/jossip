package io.rtcore.sip.channels.netty;


import java.net.SocketAddress;

import io.rtcore.sip.channels.SipClientTransport;
import io.rtcore.sip.channels.SipClientTransportFactory;
import io.rtcore.sip.channels.SipClientTransportOptions;

class NettySipClientTransportFactory implements SipClientTransportFactory {

  NettySipClientTransportFactory() {
  }

  @Override
  public SipClientTransport newClientTransport(final SocketAddress serverAddress, final SipClientTransportOptions options) {
    return new NettySipClientTransport(serverAddress, options);
  }

}
