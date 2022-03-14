package io.rtcore.sip.channels.interceptors;

import io.rtcore.sip.channels.api.SipCallOptions;
import io.rtcore.sip.channels.api.SipChannel;
import io.rtcore.sip.channels.api.SipClientExchange;
import io.rtcore.sip.channels.api.SipRequestFrame;

class InterceptorSipChannel implements SipChannel {

  private final SipChannel channel;
  private final SipClientInterceptor interceptor;

  InterceptorSipChannel(SipChannel channel, SipClientInterceptor interceptor) {
    this.channel = channel;
    this.interceptor = interceptor;
  }

  @Override
  public SipClientExchange exchange(SipRequestFrame req) {
    return interceptor.interceptCall(req, SipCallOptions.of(), this.channel);
  }

}
