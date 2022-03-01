package io.rtcore.sip.channels.errors;

import java.util.List;

import io.rtcore.sip.message.auth.headers.Authorization;
import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.SipResponseStatus;

public final class Unauthorized extends ClientFailure {

  private static final long serialVersionUID = 1L;
  private List<Authorization> challenges;

  public Unauthorized(SipResponseStatus status) {
    super(status);
  }

  public Unauthorized(SipResponse res) {
    super(res);
    this.challenges = res.getWWWAuthenticate();
  }

  public List<Authorization> authorization() {
    return challenges;
  }

}
