package io.rtcore.sip.channels.errors;

import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;

public class Redirected extends SipError {

  private static final long serialVersionUID = 1L;

  public Redirected(SipResponseStatus status) {
    super(status);
  }

  public Redirected(SipResponse res) {
    super(res);
  }

}
