package io.rtcore.sip.channels;

import java.util.concurrent.Flow;

import io.rtcore.sip.message.message.SipResponse;

/**
 * contract for dispatching incoming SIP requests for processing.
 */

public interface SipServerCallHandler {

  /**
   *
   */

  Flow.Publisher<SipResponse> startCall(SipServerCall call, Metadata metadata);

}
