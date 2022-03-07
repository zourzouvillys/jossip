package io.rtcore.sip.channels.internal;

import java.util.concurrent.Flow;

import io.rtcore.sip.message.message.SipMessage;

public interface SipStream {

  /**
   * called whenever there is at least one message available for reading.
   */

  Flow.Publisher<SipMessageProducer> messages();

  /**
   * write a message on this stream.
   */

  void writeMessage(SipMessage out);

}
