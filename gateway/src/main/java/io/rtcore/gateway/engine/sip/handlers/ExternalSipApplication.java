package io.rtcore.gateway.engine.sip.handlers;

import io.rtcore.gateway.engine.sip.SipApplication;
import io.rtcore.gateway.engine.sip.SipRequestContext;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.frame.SipRequestFrame;

/**
 * SIP application which defers to external handlers.
 *
 * for each request, a target handler is provided. this handler may provide a direct response (which
 * is fed into the context), or may return a 202 to defer responding. the handler must then call
 * back to the request resource to trigger sending a response.
 *
 */

public class ExternalSipApplication implements SipApplication {

  ExternalSipApplication() {

  }

  /**
   * process the request externally.
   */

  @Override
  public void handleRequest(final SipRequestContext ctx) {

  }

  /**
   * we should only receive in-dialog ACKs, as any for a non-2xx are end-to-end, and thus we don't
   * transmit them.
   */

  @Override
  public void handleAck(final SipRequestFrame ack, final SipAttributes attrs) {
  }

}
