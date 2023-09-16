package io.rtcore.gateway.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.rtcore.gateway.api.NICTRequest;
import io.rtcore.gateway.engine.SipEngine;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.frame.SipRequestFrame;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;

@Path("flows")
public class FlowResource {

  private static final Logger log = LoggerFactory.getLogger(FlowResource.class);

  private final SipEngine engine;

  @Inject
  public FlowResource(final SipEngine engine) {
    this.engine = engine;
  }

  /**
   * sends a request over a flow
   */

  @POST
  @Path("{flowId}/clients")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces({
    "application/json",
    "application/x-ndjson",
  })
  public void startTransaction(@PathParam("flowId") final String flowId, final NICTRequest req, @Suspended final AsyncResponse res) {

    // need to find the flow ID.
    log.info("sending SIP txn over flow {}: {}", flowId, req);

    if (SipMethods.ACK.equals(req.method())) {

      // ACK does not get any response other than accepted (or error).
      this.engine.send(
        flowId,
        FrameCreator.createRequest(req),
        new ACKResponseDelegate(res));

    }
    else if (SipMethods.INVITE.equals(req.method())) {

      // send over SIP.
      final SipRequestFrame send = FrameCreator.createRequest(req);
      final ICTResponseDelegate delegate = new ICTResponseDelegate(res);
      this.engine.send(flowId, send, delegate);

    }
    else {

      // it's a NICT, create initial frame.
      final SipRequestFrame send = FrameCreator.createRequest(req);
      final NICTResponseDelegate delegate = new NICTResponseDelegate(res);
      this.engine.send(flowId, send, delegate);

    }

  }

}
