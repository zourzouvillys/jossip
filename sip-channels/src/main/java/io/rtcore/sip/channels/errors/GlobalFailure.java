package io.rtcore.sip.channels.errors;

import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;

public class GlobalFailure extends SipError {

  private static final long serialVersionUID = 1L;

  public GlobalFailure(SipResponseStatus status) {
    super(status);
  }

  public GlobalFailure(SipResponse res) {
    super(res);
  }

}
