package io.rtcore.gateway.engine.sip;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Verify;

import io.rtcore.gateway.engine.sip.ServerTransactionStore.AbsorbAction;
import io.rtcore.gateway.engine.sip.ServerTransactionStore.Action;
import io.rtcore.gateway.engine.sip.ServerTransactionStore.ProcessAction;
import io.rtcore.gateway.engine.sip.ServerTransactionStore.ReplyAction;
import io.rtcore.gateway.udp.SipDatagramMessageHandler;
import io.rtcore.gateway.udp.SipDatagramSocket;
import io.rtcore.sip.channels.api.SipAttributes;
import io.rtcore.sip.channels.connection.SipConnections;
import io.rtcore.sip.common.ImmutableHostPort;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipMethodId;
import io.rtcore.sip.common.iana.SipMethods;
import io.rtcore.sip.common.iana.StandardSipHeaders;
import io.rtcore.sip.common.iana.StandardSipTransportName;
import io.rtcore.sip.frame.SipFrame;
import io.rtcore.sip.frame.SipRequestFrame;
import io.rtcore.sip.frame.SipResponseFrame;
import io.rtcore.sip.message.message.api.CSeq;
import io.rtcore.sip.message.message.api.SipMethod;
import io.rtcore.sip.message.message.api.Via;
import io.rtcore.sip.netty.codec.SipParsingUtils;
import io.rtcore.sip.netty.codec.udp.SipDatagramPacket;

/**
 * A {@code NetworkSegment} serves as a fundamental building block for managing
 * shared network state
 * within a SIP communication framework. It offers a powerful feature by
 * allowing the matching of
 * client and server transactions to occur within the scope of a segment, rather
 * than being tightly
 * bound to a specific transport protocol. This design decouples transaction
 * handling from transport
 * concerns and provides flexibility and resilience within a SIP-based system.
 *
 * <p>
 * In a SIP environment, a transaction represents a single request-response
 * exchange between two
 * parties, typically involving SIP endpoints. These transactions can occur over
 * various transport
 * protocols, including UDP, TCP, and/or TLS. The {@code NetworkSegment} concept
 * is particularly
 * valuable in scenarios where the characteristics of the network and transport
 * may change
 * dynamically between requests and responses.
 *
 * <p>
 * <strong>Key Benefits and Use Cases:</strong>
 * <ul>
 * <li><strong>Cross-Transport Communication:</strong> With a
 * {@code NetworkSegment}, it becomes
 * possible for a response for a transaction initially initiated over UDP to be
 * received over TCP.
 * This capability can be valuable when dealing with large SIP messages that may
 * exceed the Maximum
 * Transmission Unit (MTU) of UDP.</li>
 *
 * <li><strong>TCP Reconnection:</strong> In cases where a TCP connection needs
 * to be reestablished
 * (to the same transport address/instance), the {@code NetworkSegment} can
 * gracefully handle the
 * resending of SIP responses or requests. This is crucial in maintaining
 * session continuity and
 * preventing data loss during network disruptions.</li>
 * </ul>
 *
 * <p>
 * While the {@code NetworkSegment} class offers powerful capabilities for
 * managing network state,
 * it is important to note that it does not directly manage SIP transactions.
 * Instead, it provides a
 * shared context within which transactions can operate, allowing for flexible
 * network-state
 * tracking and routing decisions.
 *
 * We implement a server processing model which exposes the same behavior as RFC
 * 3261 (with RFC 6026
 * fixes), but doesn't have the complexity of the state-machine-per-transaction
 * model. Instead,
 * there are a few maps which provide switching decisions during transaction
 * processing:
 *
 * (1) absorb: transactions which are currently processing and have no response
 * yet. nothing should
 * be done with these, and the request should be absorbed. entries in here are
 * only present while
 * performing async processing of a request.
 *
 * (2) reply map: send the stored value back to the sender. this is used for
 * both provisional and
 * final responses (except 2xx for INVITE), these entries have a TTL, after
 * which they are removed.
 * Note that in some cases, expiry of the record indicates an unfinished (e.g,
 * errored) transaction.
 * A map implementation may chose to store these maps in some sort of read-only
 * RocksDB/Level type
 * store rather than constantly try to expire old transactions. Likewise, a
 * implementation could
 * compress the responses using heuristics from the original request.
 *
 * (3) INVITE server transaction proceeding transaction map: tracks the local
 * in-progress
 * transactions which can a CANCEL may be received for. See below for more info
 * on the CANCEL
 * handling, which may not always hit this table.
 *
 * additionally, we have a timer map which contains all the transactions which
 * are currently
 * outstanding and require retransmission (for stateful INVITE failure
 * responses). These do not
 * exist for non-INVITE server transactions, as retransmission is based on
 * re-receiving the original
 * request.
 *
 * in some cases (like with SIP responses), we could receive a CANCEL for a
 * transaction we don't
 * know about (e.g, instance is load balanced and source address changes, so
 * CANCEL goes to another
 * SIP server instance). So it would be good to handle in the edge case, but is
 * also a global
 * bottleneck as we have no way of providing some state for the CANCEL to
 * include. Therefore, we
 * assume the majority of CANCELs will end up in the right place, but the very
 * occasional one may
 * end up somewhere else. The tradeoff implementation here is to keep track of
 * in-progress ISTs. If
 * the CANCEL matches, we process. If it matches a failed transaction (using the
 * reply map), then we
 * reject it. Otherwise, it gets handed to the unknownCANCEL handler. This
 * handler can be
 * rate-limited and would only be hit in fairly infrequent edge cases in the
 * same way as the unknown
 * SIP response handler.
 *
 */

public class NetworkSegment {

  private static final Logger LOG = LoggerFactory.getLogger(NetworkSegment.class);

  /**
   *
   */

  private final ServerTransactionStore serverTransactionStore = new InMemoryServerTransactionStore();

  private final SipApplication app;

  /**
   * a map of client transaction ids to response handlers.
   */

  private Map<ClientBranchId, Consumer<SipResponseFrame>> clients = new ConcurrentHashMap<>();

  /**
   *
   */

  public NetworkSegment(final SipApplication app) {
    this.app = Objects.requireNonNull(app);
  }

  /**
   * called to handle an incoming SIP request from the SIP channel.
   *
   * at this point the request has not been checked for anything more than being
   * able to be parsed
   * into SIP header names and fields, and that the top via header field value is
   * syntactically
   * correct (if there is one).
   *
   * requests are stateless until this point, and dispatching incoming requests
   * here takes some care
   * to avoid race conditions or out of order behavior, while dispatching to
   * remote handlers. we
   * introduce the transaction model which allows short (or longer) term
   * processing of transactions.
   *
   */

  private void receiveRequest(final SipRequestFrame req, final SipAttributes attrs) {

    LOG.debug("received request method={} ruri={} {}", req.initialLine().method(), req.initialLine().uri(), attrs);

    // get the method.
    final SipMethodId method = req.initialLine().method();

    // ACK must not be handled here, sent to receiveACK instead.
    Verify.verify(method != SipMethods.ACK);

    // first we run through the fast stateless checks. these are transport specific
    // filters (e.g ip
    // address), checking for credentials (where possible without async work), and
    // other lightweight
    // non blocking work that is desirable to run before performing any state
    // manipulation. during
    // this phase, requests can be dropped (DROP), a reply sent (STATELESS_RESPONSE)
    // if it's not an
    // ACK, and optionally a transaction started. note that the method called to
    // start the
    // transaction can also respond with DROP or SEND_RESPONSE in the case where a
    // transaction has
    // already been started.

    // -- START LOCK.

    // 1: check absorb + reply map, adding to absorb if not. once we've done
    // processing we either
    // close() or commit().

    final Action action = this.serverTransactionStore.lookup(req, attrs);

    if (action instanceof final ProcessAction processor) {

      // actually process. then respond. this action will be absorbing and retaining
      // state, so it's
      // important we actually call run() on it before too long.

      processor.run(() -> {
        LOG.info("running the handler for the transaction");
        this.app.handleRequest(new SipRequestContext(this, req, attrs) {
        });
      });

    } else if (action instanceof AbsorbAction) {

      // nothing at all to do, we're still processing it or some other logic which
      // decides we don't
      // process.
      LOG.info("absorbing request");
      return;

    } else if (action instanceof final ReplyAction reply) {

      // send a reply back on the same socket as we received it.
      LOG.info("retransmitting reply: {}", reply.response());

    } else {
      // err, oh dear. a new action was added but we didn't process it. roll on sealed
      // switch
      // expressions.
      LOG.error("got invalid action from transaction store: {}", action);
    }

    // -- END LOCK.

  }

  /**
   * called when a SIP ACK message is received.
   *
   * ACK is not a request, as it doesn't generate a response. So to avoid
   * branching statements in
   * the {@link #receiveRequest(SipRequestFrame, SipAttributes)} method, we pull
   * ACK out of the
   * request call path, and handle here.
   *
   * There are three types of ACK we handle:
   *
   * (1) ACK to an INVITE we rejected (non 2xx) statefully. May or may not be
   * in-dialog.
   *
   * (2) ACK to an INVITE we rejected (non 2xx) statelessly. May or may not be
   * in-dialog.
   *
   * (3) ACK for a 2xx INVITE (stateful or stateless). These will always be
   * in-dialog.
   *
   * We can only reject an INVITE statelessly if we send a single failure response
   * without any 100
   * Trying. As soon as we send a 100, the server transaction must be handled
   * statefully.
   *
   */

  private void receiveACK(final SipRequestFrame ack, final SipAttributes attrs) {

    Verify.verify(ack.initialLine().method() == SipMethods.ACK);

    LOG.info("ACK received ruri={} {}", ack.initialLine().uri(), attrs);

  }

  /**
   * Called when a SIP response is received from a SIP channel. This method is
   * responsible for
   * matching the received SIP response with an existing client transaction that
   * is currently in
   * progress.
   *
   * <p>
   * We enforce the requirement that the SIP response must correspond to a locally
   * matching client
   * transaction, even if the SIP channel has changed. In a non-distributed
   * system, there should
   * always be client state available for handling responses. However, in
   * exceptional scenarios
   * (e.g., software crashes or bugs), where there is no matching client state, an
   * "unknownResponseHandler" is invoked to handle such responses. In a
   * distributed system, it is
   * possible to deliver these unknown responses through a side channel to the
   * instance that is
   * responsible for them. By default, these responses are just logged and
   * ignored.
   *
   * <p>
   * It is important to be aware that a SIP request may be sent over one channel,
   * but its
   * corresponding response may be received on a different channel. This behavior
   * is allowed by RFC
   * 3261, when the response size exceeds the UDP Maximum Transmission Unit (MTU)
   * but rarely
   * implemented. A real-world example of this scenario does occurs is when an
   * underlying TCP or TLS
   * stream is interrupted after receiving a request but before sending a
   * response. In such cases,
   * certain SIP implementations attempt to open a new transport connection to the
   * "sent-by" address
   * specified in the top Via header field. Therefore, it is critical to ensure
   * that the top Via
   * sent-by always resolves to the specific instance that sent the request,
   * rather than to a set of
   * load-balanced instances (or have a way of forwarding the response to the
   * right one).
   *
   * @param res
   *              The SIP response received from the SIP channel.
   * @param attrs
   *              The SIP attributes for this response frame.
   */

  private void receiveResponse(final SipResponseFrame res, final SipAttributes attrs) {

    LOG.info("received response status={} {}", res.initialLine().code(), attrs);

    // match the response to a client transaction, and push it. otherwise hand off
    // to the unknown response handler.

    // top Via ...
    final Via topVia = SipParsingUtils.topVia(res.headerLines()).orElse(null);
    SipMethod method = SipParsingUtils.cseq(res.headerLines()).map(CSeq::method).orElse(null);

    ClientBranchId transactionId = new ClientBranchId(topVia.sentBy(), method.methodId(),
        topVia.branchWithoutCookie().orElse(null));

    Consumer<SipResponseFrame> handler = this.clients.get(transactionId);

    if (handler == null) {
      LOG.info("no handler for response: {}", res);
      return;
    }

    handler.accept(res);

  }

  /**
   * an adapter to deliver incoming datagrams into the network segment with
   * {@link SipAttributes}.
   */

  public class DatagramMessageReceiverAdapter implements SipDatagramMessageHandler {

    @Override
    public void receiveDatagramFrame(final SipDatagramSocket socket, final SipDatagramPacket packet) {

      final SipFrame frame = packet.content();

      final SipAttributes.Builder attrs = SipAttributes.newBuilder()
          .set(SipNetworkAttributes.ATTR_DATAGRAM_SOCKET, socket)
          .set(SipConnections.ATTR_TRANSPORT, StandardSipTransportName.UDP)
          .set(SipConnections.ATTR_REMOTE_ADDR, packet.sender())
          .set(SipConnections.ATTR_LOCAL_ADDR, packet.recipient());

      final List<SipHeaderLine> headerLines = frame.headerLines();

      //
      final Via topVia;
      try {
        topVia = SipParsingUtils.topVia(headerLines).orElse(null);
      } catch (final Exception ex) {
        LOG.info("failed to parse incoming SIP frame top Via header: {}", ex.getMessage(), ex);
        // just return. no point in anything else.
        return;
      }

      if (topVia != null) {

        // TODO: should we check that the Via's sent-transport value matches UDP?

        // set the sent-by values.
        attrs.set(SipConnections.ATTR_SENT_BY, ImmutableHostPort.copyOf(topVia.sentBy()));

        // we allow requests missing a Via altogether and also those missing a branch
        // with via
        // cookie. in this case, the SipAttribute set will just not include the
        // ATTR_BRANCH_ID
        // value. it is the responsibility of the downstream handler to validate based
        // on the
        // application the stack is being used with.
        topVia.branchWithoutCookie().ifPresent(branchId -> attrs.set(SipConnections.ATTR_BRANCH_ID, branchId));

      }

      if (frame instanceof final SipRequestFrame req) {

        if (req.initialLine().method().toStandard() == SipMethods.ACK) {
          // ACK hits different. it never generates a response, even if in-dialog or an
          // error occurs
          // while processing it.
          NetworkSegment.this.receiveACK(req, attrs.build());
        } else {
          NetworkSegment.this.receiveRequest(req, attrs.build());
        }
      } else if (frame instanceof final SipResponseFrame res) {
        NetworkSegment.this.receiveResponse(res, attrs.build());
      } else {
        // this should never happen, as we have sealed interfaces for the SipFrame.
        LOG.warn("received SIP datagram frame of unknown type.");
      }

    }

  }

  public ServerTransactionStore serverStore() {
    return this.serverTransactionStore;
  }

  public Runnable allocateClientTransaction(ClientBranchId transactionId, SipMethodId method,
      Consumer<SipResponseFrame> responseHandler) {
    this.clients.put(transactionId, responseHandler);
    return () -> {
      LOG.info("transaction handle for {} / {} removed", transactionId, method);
      this.clients.remove(transactionId);
    };
  }

}
