package io.rtcore.gateway.rest;

import java.net.URI;

import io.rtcore.gateway.api.ImmutableSipResponsePayload;
import io.rtcore.gateway.api.NICTRequest;
import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.sip.common.SipHeaders;
import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;

final class FrameCreator {

  static SipRequestFrame createRequest(final NICTRequest req) {

    final SipMethodId method = req.method();
    final URI ruri = URI.create(req.uri().orElse("sip:invalid"));
    final SipHeaders headers = req.headers();

    return SipRequestFrame.of(method, ruri, headers).withBody(req.body());

  }

  public static SipResponsePayload createResponsePayload(final SipResponseFrame res) {
    return ImmutableSipResponsePayload.builder()
      .statusCode(res.initialLine().code())
      .reasonPhrase(res.initialLine().reason())
      .headers(SipHeaders.of(res.headerLines()))
      .body(res.body())
      .build();
  }

}
