package io.rtcore.gateway.engine.sip;

import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;

public class SipRequestContext {

  public SipRequestContext(final NetworkSegment networkSegment, final SipRequestFrame req, final SipAttributes attrs) {
  }

  /**
   * provide a response. if the request context is an INVITE and the response is a 2xx, multiple may
   * be sent. otherwise, only a single final response is allowed.
   */

  public void reply(final SipResponseFrame response) {
  }

  /**
   * close this context. is idempotent, so may be called even if already closed.
   *
   * once this has been called, reply may not be called. any further requests will create a new
   * SipRequestContext unless a transaction has been created.
   *
   */

  public void close() {
  }

}
