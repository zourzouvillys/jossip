package io.rtcore.sip.channels;

import java.util.concurrent.Flow;

import io.rtcore.sip.message.message.SipResponse;

public interface SipServerCallHandler {

  /**
   *
   */

  Flow.Publisher<SipResponse> startCall(SipServerCall call);

}