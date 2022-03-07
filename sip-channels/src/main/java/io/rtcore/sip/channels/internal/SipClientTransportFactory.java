package io.rtcore.sip.channels.internal;

import java.net.SocketAddress;

/**
 * factory for providing new instances of {@link SipClientTransport}.
 */

public interface SipClientTransportFactory {

  SipClientTransport newClientTransport(final SocketAddress serverAddress, final SipClientTransportOptions options);

}
