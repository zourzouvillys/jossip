package io.rtcore.gateway.rest;

import io.rtcore.gateway.api.NICTRequest;
import io.rtcore.gateway.engine.SipEngine;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.frame.SipRequestFrame;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.container.AsyncResponse;
import jakarta.ws.rs.container.Suspended;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("clients")
public class NICTResource {

  private final SipEngine engine;

  @Inject
  public NICTResource(final SipEngine engine) {
    this.engine = engine;
  }

  /**
   * sends a request.
   *
   * for NICT, it can not have provisional responses, and can not fork - so there is only a single
   * response and it is the final (non 2xx) SIP response or error.
   *
   * for IST there can be a 100, 1XX, then one or more 2XXs or a single final failure. note that
   * because of end-to-end ACKs for 2XX, more than one 2XX may be delivered. for the API we provide
   * a
   *
   */

  @PUT
  @Path("{txnId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces({
    "application/json",
    "application/x-ndjson",
  })
  public void startTransaction(@PathParam("txnId") final String txnId, final NICTRequest req, @Suspended final AsyncResponse res) {

    if (SipMethods.ACK.equals(req.method())) {

      // ACK does not get any response other than accepted (or error).
      this.engine.send(FrameCreator.createRequest(req), new ACKResponseDelegate(res));

    }
    else if (SipMethods.INVITE.equals(req.method())) {

      // send over SIP.
      final SipRequestFrame send = FrameCreator.createRequest(req);
      final ICTResponseDelegate delegate = new ICTResponseDelegate(res);
      this.engine.send(send, delegate);

    }
    else {

      // it's a NICT, create initial frame.
      final SipRequestFrame send = FrameCreator.createRequest(req);
      final NICTResponseDelegate delegate = new NICTResponseDelegate(res);
      this.engine.send(send, delegate);

    }

  }

  @Path("{txnId}")
  public Response getTransaction(@PathParam("txnId") final String txnId) {
    return Response.noContent().build();
  }

}
