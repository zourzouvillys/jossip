package io.rtcore.gateway.rest;

import io.rtcore.gateway.engine.OutgoingRequestDelegate;
import io.rtcore.sip.channels.api.SipResponseFrame;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.core.Response;

final class NICTResponseDelegate implements OutgoingRequestDelegate {

  private final AsyncResponse res;

  NICTResponseDelegate(final AsyncResponse res) {
    this.res = res;
  }

  @Override
  public void onResponse(final SipResponseFrame res) {
    if (this.res.isDone()) {
      return;
    }
    this.res.resume(Response.ok(FrameCreator.createResponsePayload(res)).build());
  }

  @Override
  public void onError(final Throwable t) {
    if (this.res.isDone()) {
      return;
    }
    if (t instanceof RuntimeException) {
      this.res.resume(Response.serverError().entity(t.getMessage()).build());
    }
    else {
      this.res.resume(t);
    }
  }

  @Override
  public void onComplete() {
    if (this.res.isDone()) {
      return;
    }
    this.res.resume(Response.noContent().build());
  }

}
