package io.rtcore.sip.proxy.client;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

import io.rtcore.sip.proxy.MessageWriter;

/**
 * takes responsibility for transmitting a request over the network, retransmitting as needed and
 * then submitting a final response back to the invoker.
 * 
 * the TU in this scenario is us, therefore we add a Via entry. a correlation ID may be provided by
 * the requestor, in which case it will be included in any responses or notifications.
 * 
 * some scenarios
 * 
 */

public class SipClientManager {

  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SipClientManager.class);

  private final MessageWriter writer;

  public SipClientManager(MessageWriter writer) {
    this.writer = writer;
  }

  /**
   * start a transaction to a remote client.
   * 
   * only SIP messages may be sent this way. other types of message (keepalives, stun packets etc)
   * must use the transport directly.
   * 
   */

  public void send(InetSocketAddress target, ByteBuffer payload) {
    log.debug("sending to {}", target);
    this.writer.write(target, target, payload);
  }

}
