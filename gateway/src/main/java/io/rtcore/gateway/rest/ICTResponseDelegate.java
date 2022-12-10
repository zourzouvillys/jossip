package io.rtcore.gateway.rest;

import java.io.IOException;

import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;

import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.gateway.engine.OutgoingRequestDelegate;
import io.rtcore.sip.channels.api.SipResponseFrame;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.core.Response;

final class ICTResponseDelegate implements OutgoingRequestDelegate {

  private static final Logger log = LoggerFactory.getLogger(ICTResponseDelegate.class);

  private final AsyncResponse res;
  private ChunkedOutput<Object> output;

  ICTResponseDelegate(final AsyncResponse res) {
    this.res = res;
  }

  @Override
  public void onResponse(final SipResponseFrame res) {

    log.info("onResponse({})", res.initialLine());

    final SipResponsePayload body = FrameCreator.createResponsePayload(res);

    if (!this.res.isDone()) {

      // if this is a single final response, no need to switch to x-ndjson.

      if (res.initialLine().code() >= 300) {
        // send just this one, no need for chunked encoding.
        this.res.resume(Response.ok(body, "application/json").build());
        return;
      }

      // there may be a numebr of responses so send as chunked.
      this.output = new ChunkedOutput<>(JsonNode.class, "\n");
      this.res.resume(Response.ok(this.output, "application/x-ndjson").build());

    }

    try {
      this.output.write(FrameCreator.createResponsePayload(res));
    }
    catch (final IOException e) {
      // TODO Auto-generated catch block
      throw new RuntimeException(e);
    }

  }

  @Override
  public void onError(final Throwable t) {

    log.info("onError({})", t.getMessage());

    if (this.res.isDone()) {
      // we send response already, not much to so here.
      try {
        this.output.close();
      }
      catch (final IOException e) {
        // TODO Auto-generated catch block
        throw new RuntimeException(e);
      }
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

    log.info("onComplete");

    if (this.res.isDone()) {
      try {
        if (this.output != null) {
          this.output.close();
        }
      }
      catch (final IOException e) {
        // TODO Auto-generated catch block
        throw new RuntimeException(e);
      }
      return;
    }

    // we shouldn't ever get onComplete without opening ... this is error.
    log.error("invalid state for onComplete");

  }

}
