package io.rtcore.sip.channels.netty;


import java.net.SocketAddress;

import io.rtcore.sip.channels.internal.SipClientTransport;
import io.rtcore.sip.channels.internal.SipClientTransportFactory;
import io.rtcore.sip.channels.internal.SipClientTransportOptions;

class NettySipClientTransportFactory implements SipClientTransportFactory {

  NettySipClientTransportFactory() {
  }

  @Override
  public SipClientTransport newClientTransport(final SocketAddress serverAddress, final SipClientTransportOptions options) {
    return new NettySipClientTransport(serverAddress, options);
  }

}
