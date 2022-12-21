package io.rtcore.gateway.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;

import io.netty.channel.nio.NioEventLoopGroup;
import io.rtcore.sip.channels.api.SipRequestFrame;
import io.rtcore.sip.channels.connection.SipConnection;
import io.rtcore.sip.channels.connection.SipRoute;
import io.rtcore.sip.common.iana.SipMethods;

public class SipEngine {

  private static final Logger log = LoggerFactory.getLogger(SipEngine.class);

  private final ServerTxnStore serverTxnStore;
  private final SipSegment segment;
  private final NioEventLoopGroup eventLoopGroup;
  private final ConnectionStore connections;

  SipEngine(final BackendProvider backendProvider, final SipRoute route) {
    this.eventLoopGroup = new NioEventLoopGroup(1);
    this.serverTxnStore = new ServerTxnStore(backendProvider.serverResponseInterceptor());
    this.connections = new ConnectionStore();
    this.segment = new SipSegment(backendProvider, this.serverTxnStore, this.eventLoopGroup, this.connections, route);
  }

  public void send(final SipRequestFrame req, final OutgoingRequestDelegate delegate) {
    this.segment.send(req, delegate);
  }

  public void send(final String flowId, final SipRequestFrame req, final OutgoingRequestDelegate delegate) {

    log.info("transmitting request over flow {}: {}", flowId, req);

    // fetch a connection.
    final SipConnection conn = this.connections.lookup(flowId).orElseThrow(() -> Problem.valueOf(Status.GONE, "flow failed"));

    if (req.initialLine().method() == SipMethods.ACK) {
      conn.send(SipSegment.fixupRequest(req, conn))
        .handle((res, err) -> {
          if (err != null) {
            delegate.onError(err);
          }
          else {
            delegate.onComplete();
          }
          return null;
        });
      return;
    }

    // perform the exchange over the connection.
    conn.exchange(SipSegment.fixupRequest(req, conn))
      .responses()
      .subscribe(
        res -> delegate.onResponse(res.response()),
        delegate::onError,
        delegate::onComplete);

  }

  public ServerTxnHandle lookup(final String txnKey) {
    return this.serverTxnStore.lookup(txnKey);
  }

}
