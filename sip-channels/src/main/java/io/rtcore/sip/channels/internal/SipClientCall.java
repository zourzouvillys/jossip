package io.rtcore.sip.channels.internal;

import java.util.Arrays;
import java.util.concurrent.Flow;

import io.rtcore.sip.message.message.SipResponse;
import io.rtcore.sip.message.message.api.Reason;

/**
 *
 */

public interface SipClientCall extends Flow.Publisher<SipResponse> {

  /**
   * attempt to cancel this client call if possible.
   */

  SipClientCall cancel(Iterable<Reason> reason);

  /**
   * attempt to cancel this client call if possible.
   */

  default SipClientCall cancel(final Reason... reasons) {
    return this.cancel(Arrays.asList(reasons));
  }

}
