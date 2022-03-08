package io.rtcore.sip.channels.api;

import io.rtcore.sip.channels.internal.SipClientStream;

public interface SipTransport {

  /**
   * open a new logical stream for sending requests over.
   */

  SipClientStream newStream();

}
