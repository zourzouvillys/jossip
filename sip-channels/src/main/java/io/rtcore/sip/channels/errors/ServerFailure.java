package io.rtcore.sip.channels.errors;

import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;

public class ServerFailure extends SipError {

  private static final long serialVersionUID = 1L;

  public ServerFailure(SipResponseStatus status) {
    super(status);
  }

  public ServerFailure(SipResponse res) {
    super(res);
  }

}
