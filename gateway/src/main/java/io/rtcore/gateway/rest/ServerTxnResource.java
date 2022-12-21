package io.rtcore.gateway.rest;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.gateway.engine.ServerTxnHandle;
import io.rtcore.gateway.engine.SipEngine;
import jakarta.inject.Inject;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("server")
public class ServerTxnResource {

  private static final Logger log = LoggerFactory.getLogger(ServerTxnResource.class);

  private final SipEngine engine;

  @Inject
  public ServerTxnResource(final SipEngine engine) {
    this.engine = engine;
  }

  /**
   * provide a response to an ongoing server tranaction.
   */

  @Path("txn/{txnKey}")
  @POST
  public Response respondTransaction(@PathParam("txnKey") final String txnKey, final SipResponsePayload res) {

    final ServerTxnHandle txn = this.engine.lookup(txnKey);

    if (txn == null) {
      log.warn("couldn't find server txn {}", txnKey);
      throw Problem.builder()
        .withType(URI.create("https://github.com/zourzouvillys/rtcore#server-txn-not-found"))
        .withStatus(Status.NOT_FOUND)
        .withTitle("server transaction not found")
        .withDetail(String.format("server txn with id %s not found", txnKey))
        .build();
    }

    log.info("processing response for ongoing txn {}", txnKey, res);

    txn.respond(res);

    return Response.accepted().build();

  }

  @Path("txn/{txnId}")
  @DELETE
  public Response closeTransaction(@PathParam("txnKey") final String txnKey) {
    final ServerTxnHandle txn = this.engine.lookup(txnKey);
    if (txn == null) {
      return Response.status(Response.Status.NOT_FOUND).build();
    }
    log.info("deleting response for ongoing txn {}", txnKey);
    txn.close();
    return Response.ok().build();
  }

}
