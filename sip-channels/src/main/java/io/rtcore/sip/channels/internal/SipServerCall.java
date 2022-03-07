package io.rtcore.sip.channels.internal;

import java.util.Optional;

import io.rtcore.sip.common.Host;
import io.rtcore.sip.message.message.SipRequest;

/**
 * Encapsulates a single incoming SIP request from a UA.
 */

public interface SipServerCall extends Attributed {

  /**
   * the authority this request is addressed to.
   */

  Optional<Host> authority();

  /**
   * the incoming SIP request.
   */

  SipRequest request();

}
