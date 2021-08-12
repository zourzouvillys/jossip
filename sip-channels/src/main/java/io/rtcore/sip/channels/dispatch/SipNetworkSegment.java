package io.rtcore.sip.channels.dispatch;

import java.util.concurrent.Flow;

import io.rtcore.sip.common.HostPort;
import io.rtcore.sip.message.message.SipResponse;

/**
 * a segment is the namespace for mapping incoming responses from transports to consumers.because
 * multiple transports can be in the same segment (e.g, UDP + TCP), we sometimes can't match
 * branches on just the transport the response was received on.
 *
 * the {@link SipNetworkSegment} is not responsible for actual transmission of requests, nor of
 * processing any responses in any form. it just provides a funnel for mapping incoming responses to
 * the right place, or indicating to the transport of a response which is not expected.
 *
 * A {@link SipNetworkSegment} is not responsible for any transaction behavior; this remains the
 * responsibility of the request sender. generally, a sender will first select the target transport,
 * then generate a branch id, and start listening on the {@link SipNetworkSegment}. once subscribed,
 * it will then transmit the request with the Via header it added. once a response is received and
 * it is clear no further ones will be (e.g, final response) then the subscriber is closed which
 * removes the listening for this branch. note that with some transactions (e.g, those sent over
 * UDP), we will often keep the listener around longer than the request/response cycle to absorb
 * retransmits. some heuristics can be applied to know when it is unlikely to have any duplicate
 * response transmissions that would need be absorbed, e.g a NIST where we only transmitted a single
 * request, and received a single final response. as we drop spurious responses, the risk here is
 * just logs showing such spurious responses.
 *
 * in some scenarios, a {@link SipNetworkSegment} will be created for a set of transports and in
 * others it will match just a single transport. note well that balancing incoming flows over
 * multiple servers means that this will fail if a UAS sends a response on a different flow.
 * although it is not common, it can happen if a flow fails before sending the response, or if a
 * response is too large for a datagram and thus "upgraded" to a stream.
 *
 * however, both of these are edge cases and in most deployment scenarios will not be at all
 * relevant.
 *
 */

public interface SipNetworkSegment {

  /**
   * a publisher which will provide any responses received with the specified branch id to any
   * subscribers.
   *
   * if a response is received in a segment and it does match any active subscribers or absorbed
   * ones, it will be provided to the spurious response handler.
   *
   * note that the segment will only start capturing for the specified branch ID after the
   * subscription has been started, and will only last until the subscription is terminated.
   *
   * beware of the potential async nature of subscribing. do not send any requests until the
   * subscription is ready, e.g request(n) has been called.
   *
   * @param sentBy
   *          The sent-by field of the first Via.
   *
   * @param branchId
   *          The branch identifier, without the magic cookie. it is case sensitive.
   *
   * @return A publisher which will provide all responses (including retransmissions) from
   *         transports in this network segment where the response matched the sent-by and branch-id
   *         value of the top via, until it is cancelled.
   *
   */

  Flow.Publisher<SipResponse> responses(HostPort sentBy, String branchId);

}
