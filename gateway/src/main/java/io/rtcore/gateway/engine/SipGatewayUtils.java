package io.rtcore.gateway.engine;

import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.frame.SipFrameUtils;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;

public class SipGatewayUtils {

  public static SipResponseFrame toResponseFrame(final SipRequestFrame req, final SipResponsePayload res) {
    return SipFrameUtils.createResponse(
      req,
      SipStatusCodes.forStatusCode(res.statusCode()),
      res.headers().lines(),
      res.body());
  }


}
