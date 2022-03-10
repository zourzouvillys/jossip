package io.rtcore.sip.channels.errors;

import java.util.Objects;

import io.rtcore.sip.channels.api.SipResponseFrame;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;

public class SipError extends RuntimeException {

  private static final long serialVersionUID = 1L;
  private final SipResponseStatus status;

  public SipError(SipResponseStatus status) {
    super(status.toString());
    this.status = Objects.requireNonNull(status);
  }

  public SipError(SipResponse res) {
    this(res.getStatus());
  }

  public SipError(SipResponseFrame res) {
    this(SipResponseStatus.of(res.initialLine()));
  }

  public SipResponseStatus status() {
    return this.status;
  }

}
