package io.rtcore.gateway.engine.sip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.frame.SipRequestFrame;

public interface SipApplication {

  Logger LOG = LoggerFactory.getLogger(SipApplication.class);

  /**
   * when a new SIP request is received. there is no transaction, but the context must be responded
   * to or closed otherwise state will be leaked. a transaction can be created at any point by
   * calling commit() on the context.
   */

  void handleRequest(SipRequestContext ctx);

  /**
   * when an ACK that isn't part of a SIP failure we previously sent back is received. (as these are
   * hop by hop).
   */

  default void handleAck(final SipRequestFrame ack, final SipAttributes attrs) {
    LOG.warn("dropping unknown ACK");
  }

}
