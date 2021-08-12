package io.rtcore.sip.channels;

import io.rtcore.sip.message.message.SipRequest;

/**
 * a logical set of sip servers and clients which share a single transaction space.
 */

public interface SipSegment extends SipChannel {

  /**
   * start a new client exchange on this segment.
   */

  @Override
  SipClientCall exchange(SipRequest req, SipCallOptions options);

}
