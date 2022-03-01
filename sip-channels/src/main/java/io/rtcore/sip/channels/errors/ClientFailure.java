package io.rtcore.sip.channels.errors;

import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;

public class ClientFailure extends SipError {

  private static final long serialVersionUID = 1L;

  public ClientFailure(SipResponseStatus status) {
    super(status);
  }

  public ClientFailure(SipResponse res) {
    super(res);
  }

}
