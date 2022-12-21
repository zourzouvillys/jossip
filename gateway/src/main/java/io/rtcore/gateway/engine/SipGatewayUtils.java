package io.rtcore.gateway.engine;

import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.sip.channels.api.SipFrameUtils;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.common.iana.SipStatusCodes;

public class SipGatewayUtils {

  public static SipResponseFrame toResponseFrame(final SipRequestFrame req, final SipResponsePayload res) {
    return SipFrameUtils.createResponse(
      req,
      SipStatusCodes.forStatusCode(res.statusCode()),
      res.headers().lines(),
      res.body());
  }


}
