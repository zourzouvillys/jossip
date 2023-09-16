package io.rtcore.gateway.engine;

import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.frame.SipFrameUtils;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;

public interface ServerTxnHandle {

  String id();

  /**
   * no need to close if the response is a final failure, or a 2xx and non-invite.
   */

  default void respond(final SipResponsePayload res) {
    this.respond(SipGatewayUtils.toResponseFrame(this.request(), res));
  }

  void respond(SipResponseFrame res);

  void close();

  SipRequestFrame request();

  void close(Throwable ex);

  default void respond(final SipStatusCodes status) {
    this.respond(SipFrameUtils.createResponse(this.request(), status));
  }

}
