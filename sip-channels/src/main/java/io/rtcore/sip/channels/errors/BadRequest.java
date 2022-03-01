package io.rtcore.sip.channels.errors;

import io.rtcore.sip.message.message.SipResponseStatus;

public class BadRequest extends ClientFailure {

  private static final long serialVersionUID = 1L;

  public BadRequest() {
    super(SipResponseStatus.BAD_REQUEST);
  }

}
