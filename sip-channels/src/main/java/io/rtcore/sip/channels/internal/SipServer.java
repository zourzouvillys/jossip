package io.rtcore.sip.channels.internal;

import java.util.concurrent.Flow;

/**
 * provides an API for accepting incoming transport connections.
 */

public interface SipServer {

  /**
   * called to indicate at least one new transport is ready.
   */

  Flow.Publisher<SipTransportAcceptor> incomingTransports();

}
