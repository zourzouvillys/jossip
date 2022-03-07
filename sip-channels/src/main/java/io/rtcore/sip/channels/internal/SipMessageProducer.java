package io.rtcore.sip.channels.internal;

import java.io.IOException;

import io.rtcore.sip.message.message.SipMessage;

public interface SipMessageProducer {

  /**
   * attempt a read of a SIP message.
   *
   * @return
   * @throws IOException
   */

  SipMessage next() throws IOException;

}
