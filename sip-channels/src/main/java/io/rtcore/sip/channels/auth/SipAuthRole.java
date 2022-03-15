package io.rtcore.sip.channels.auth;

import io.rtcore.sip.common.iana.SipHeaderId;
import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.common.iana.StandardSipHeaders;

public enum SipAuthRole {

  PROXY(
        SipStatusCodes.PROXY_AUTHENTICATION_REQUIRED,
        StandardSipHeaders.PROXY_AUTHENTICATE,
        StandardSipHeaders.PROXY_AUTHORIZATION),

  USER_AGENT(
             SipStatusCodes.UNAUTHORIZED,
             StandardSipHeaders.WWW_AUTHENTICATE,
             StandardSipHeaders.AUTHORIZATION)

  ;

  private final StandardSipHeaders challengeRequest;
  private final StandardSipHeaders challengeResponse;
  private final SipStatusCodes statusCode;

  private SipAuthRole(SipStatusCodes statusCode, StandardSipHeaders challengeRequest, StandardSipHeaders challengeResponse) {
    this.statusCode = statusCode;
    this.challengeRequest = challengeRequest;
    this.challengeResponse = challengeResponse;
  }

  public SipStatusCodes statusCode() {
    return this.statusCode;
  }

  public SipHeaderId challengeRequestHeader() {
    return this.challengeRequest;
  }

  public SipHeaderId challengeResponseHeader() {
    return this.challengeResponse;
  }

}
