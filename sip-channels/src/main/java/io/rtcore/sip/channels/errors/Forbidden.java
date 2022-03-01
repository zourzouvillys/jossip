package io.rtcore.sip.channels.errors;

import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;

public class Forbidden extends ClientFailure {

  private static final long serialVersionUID = 1L;

  public Forbidden(SipResponseStatus status) {
    super(status);
  }

  public Forbidden(SipResponse res) {
    super(res);
  }

}
