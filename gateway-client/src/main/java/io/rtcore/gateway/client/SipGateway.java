package io.rtcore.gateway.client;

import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import io.rtcore.gateway.api.SipResponsePayload;
import io.rtcore.sip.common.SipHeaderLine;
import io.rtcore.sip.common.iana.SipMethodId;

public interface SipGateway {

  interface ClientInviteDelegate {

    void onNext(SipResponsePayload res);

    default void onComplete() {
    }

    void onError(Throwable t);

  }

  /**
   * Send an INVITE and receive the responses.
   *
   * SIP INVITE transactions will send multiple 2XX responses, and each one must be passed end to
   * end as the client needs to send the end of end ACK for each one. This is because the ACK for
   * 2xx INVITE responses are end to end not hop by hop like the ACK for an INVITE failure.
   *
   * @param uri
   *          The R-URI.
   * @param headers
   *          The request headers.
   * @param body
   *          A body to send, if any.
   * @param delegate
   *          called for each client response.
   *
   * @return future which completes after onError or onComplete is called on the delegate.
   */

  CompletableFuture<?> INVITE(URI uri, Collection<SipHeaderLine> headers, Optional<String> body, ClientInviteDelegate delegate);

  /**
   * Send an in-dialog ACK.
   *
   * @param uri
   *          The R-URI.
   * @param headers
   *          The ACK message headers.
   *
   * @return future which completes once it's been transmitted over the wire.
   */

  CompletableFuture<?> ACK(URI uri, Collection<SipHeaderLine> headers, Optional<String> body);

  /**
   * send a non-INVITE request.
   *
   * @param method
   *          The method, can not be INVITE or ACK.
   * @param uri
   *          The R-URI.
   * @param headers
   *          The headers to send.
   * @param body
   *          request bodt.
   *
   * @return completes with the final SIP response, or error if unable to receive a SIP response.
   *
   */

  CompletableFuture<SipResponsePayload> request(SipMethodId method, URI uri, Collection<SipHeaderLine> headers, Optional<String> body);

}
