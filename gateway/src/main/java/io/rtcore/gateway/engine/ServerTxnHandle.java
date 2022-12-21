package io.rtcore.gateway.engine;

import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.sip.channels.api.SipFrameUtils;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.common.iana.SipStatusCodes;

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
