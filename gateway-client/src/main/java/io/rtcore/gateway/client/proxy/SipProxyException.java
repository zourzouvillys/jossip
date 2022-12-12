package io.rtcore.gateway.client.proxy;

import io.rtcore.sip.common.iana.SipStatusCodes;

public class SipProxyException extends RuntimeException {

  private static final long serialVersionUID = 1L;
  private final SipStatusCodes status;

  public SipProxyException(final SipStatusCodes status) {
    super(String.format("%d %s", status.statusCode(), status.reasonPhrase()));
    this.status = status;
  }

  public SipStatusCodes status() {
    return this.status;
  }

}
