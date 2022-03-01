package io.rtcore.sip.channels.errors;

import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;

public class MethodNotAllowed extends ClientFailure {

  private static final long serialVersionUID = 1L;

  public MethodNotAllowed(SipResponseStatus status) {
    super(status);
  }

  public MethodNotAllowed(SipResponse res) {
    super(res);
  }

}
