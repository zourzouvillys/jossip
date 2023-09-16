package io.rtcore.gateway.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rtcore.gateway.engine.OutgoingRequestDelegate;
import io.rtcore.sip.frame.SipResponseFrame;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.core.Response;

public class ACKResponseDelegate implements OutgoingRequestDelegate {

  private static final Logger log = LoggerFactory.getLogger(ACKResponseDelegate.class);

  private final AsyncResponse res;

  public ACKResponseDelegate(final AsyncResponse res) {
    this.res = res;
  }

  @Override
  public void onResponse(final SipResponseFrame res) {
    // we shouldn't ever get a response for an ACK.
    log.warn("unexpeccted response to ACK");
  }

  @Override
  public void onComplete() {
    log.info("ACK send complete");
    this.res.resume(Response.accepted().build());
  }

  @Override
  public void onError(final Throwable t) {
    log.warn("ACK send was error");
    this.res.resume(Response.serverError().build());
  }

}
