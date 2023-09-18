package io.rtcore.gateway.engine.sip.handlers;

import io.rtcore.gateway.engine.sip.SipApplication;
import io.rtcore.gateway.engine.sip.SipRequestContext;

/**
 * SIP application which defers to external handlers.
 *
 * for each request, a target handler is provided. this handler may provide a direct response (which
 * is fed into the context), or may return a 202 to defer responding. the handler must then call
 * back to the request resource to trigger sending a response.
 *
 */

public class ExternalSipApplication implements SipApplication {

  /**
   * process the request externally.
   *
   * the endpoint handler can provide the response directly, or it can be provided later by
   * submitting to the request endpoint. this allows for HTTP endpoints which return the response
   * directly, or by sending a 202 and then POSTing to the request. the later allows SNS, kinesis,
   * or other endpoints to receive a notification of the request, but trasnsmission of the responses
   * happen directly between the external handler and us, not as part of the response to the
   * notification. if async handlers are used, at very least a 100 response needs to be provided for
   * INVITEs, that contains the endpoint to use for notifying of CANCEL. If it is not provided, a
   * 100 will be sent by us, and the CANCEL responds with 200 OK, but nothing is done.
   *
   */

  @Override
  public void handleRequest(final SipRequestContext ctx) {
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented Method: SipApplication.handleRequest invoked.");
  }

}
