package io.rtcore.sip.channels.errors;

import io.rtcore.sip.common.iana.SipStatusCodes;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;

public final class SipErrors {

  public static ClientFailure methodNotAllowed() {
    return new ClientFailure(SipResponseStatus.METHOD_NOT_ALLOWED);
  }

  public static SipError fromResponse(SipResponse res) {

    SipStatusCodes std = res.getStatus().asStandardCode().orElse(null);

    if (std != null) {
      switch (std) {
        case UNAUTHORIZED:
          return new Unauthorized(res);
        case FORBIDDEN:
          return new Forbidden(res);
        case METHOD_NOT_ALLOWED:
          return new MethodNotAllowed(res);
        case PROXY_AUTHENTICATION_REQUIRED:
          return new ProxyAuthenticationRequired(res);
        default:
          break;
      }
    }

    switch (res.getStatus().category()) {
      case TRYING:
      case PROVISIONAL:
      case SUCCESSFUL:
        throw new IllegalArgumentException("can't generate an error from a non failure response code");
      case REDIRECTION:
        return new Redirected(res);
      case REQUEST_FAILURE:
        return new ClientFailure(res);
      case SERVER_FAILURE:
        return new ServerFailure(res);
      case GLOBAL_FAILURE:
        return new GlobalFailure(res);
    }

    throw new IllegalArgumentException();

  }

}
