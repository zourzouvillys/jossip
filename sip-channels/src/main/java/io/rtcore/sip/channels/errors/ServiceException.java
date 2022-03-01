package io.rtcore.sip.channels.errors;

import io.rtcore.sip.message.message.SipResponseStatus;

public class ServiceException extends ServerFailure {

  public ServiceException(SipResponseStatus status) {
    super(SipResponseStatus.SERVICE_UNAVAILABLE);
  }

  private static final long serialVersionUID = 1L;

}
