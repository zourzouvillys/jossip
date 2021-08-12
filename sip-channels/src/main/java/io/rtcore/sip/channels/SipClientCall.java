package io.rtcore.sip.channels;


import java.util.concurrent.Flow;

import io.rtcore.sip.message.message.SipResponse;

public interface SipClientCall extends Flow.Publisher<SipResponse> {

  /**
   * an optional cancellation operation.
   */

  // SipClientCall cancel(List<Reason> reason);

}
