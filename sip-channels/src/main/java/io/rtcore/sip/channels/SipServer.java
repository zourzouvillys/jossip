package io.rtcore.sip.channels;

import java.util.concurrent.Flow;

public interface SipServer {

  /**
   * called to indicate at least one new transport is ready.
   */

  Flow.Publisher<SipTransportAcceptor> incomingTransports();

}
