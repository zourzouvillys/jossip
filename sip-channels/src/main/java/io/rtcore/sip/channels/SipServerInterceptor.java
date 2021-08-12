package io.rtcore.sip.channels;

import java.util.concurrent.Flow;

import io.rtcore.sip.message.message.SipResponse;

public interface SipServerInterceptor extends SipChannelInterceptor {

  Flow.Publisher<SipResponse> interceptCall(SipServerCall call, SipServerCallHandler next);

}
