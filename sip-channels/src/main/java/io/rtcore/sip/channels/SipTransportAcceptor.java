package io.rtcore.sip.channels;

public interface SipTransportAcceptor {

  /**
   * accept the next transport. once returned, the caller is responsible for the lifecycle
   * management of this transport.
   */

  ManagedSipChannel next();

}
