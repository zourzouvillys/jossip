package io.rtcore.sip.channels;

import io.rtcore.sip.message.message.SipRequest;

/**
 * a channel which messages can be exchanged with, in general the standard request->reply model.
 *
 * when INVITE is SipRequest -> (100?, 1XX*, (2XX+ | [3-6]XX)?). The ACK for a failure is not
 * signaled (it's handled by the individual transport as needed). The ACK for a 2XX should be
 * handled as a new exchange
 *
 * when ACK, they may be retransmitted. the Call should remain open until the original INVITE is
 * closed. no responses will ever be received... transport errors may be though.
 *
 * An INVITE may be cancelled using cancel(Reason).
 *
 * when a non-INVITE, it's the expected SipRequest -> (100?, 1XX*, [2-6]XX) sequence.
 *
 */

public interface SipChannel {

  /**
   * start a new request over this SipChannel.
   */

  SipClientCall exchange(SipRequest sender, SipCallOptions options);

  /**
   *
   */

  default SipClientCall exchange(final SipRequest request) {
    return this.exchange(request, SipCallOptions.of());
  }

}
